package com.bochkov.smallcraft.wicket.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.model.LoadableDetachableModel;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CompositeEntityModel<T> extends LoadableDetachableModel<T> {

    Map<String, Pair> propertyKeys = new HashMap<>();

    Class<T> clazz;

    public CompositeEntityModel(Class<T> clazz) {
        this.clazz = clazz;
    }

    public CompositeEntityModel(T object, Class<T> clazz) {
        super(object);
        this.clazz = clazz;
    }

    @Override
    protected T load() {
        T object = null;
        if (!propertyKeys.isEmpty()) {
            object = BeanUtils.instantiateClass(clazz);
            for (String prop : propertyKeys.keySet()) {
                Pair pair = propertyKeys.get(prop);
                PropertyDescriptor d = BeanUtils.getPropertyDescriptor(clazz, pair.getValue());
                Object propValue = loadProp(pair);
                try {
                    if (d != null) {
                        d.getWriteMethod().invoke(object, propValue);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    @Override
    protected void onDetach() {
        Object o = getObject();
        Map<String, Pair> map = Maps.newHashMap();
        if (o != null) {
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getWriteMethod() != null) {
                    if (pd.getReadMethod() != null) {
                        try {
                            Object v = pd.getReadMethod().invoke(o);
                            Class propertyType = pd.getPropertyType();
                            String s = Application.get().getConverterLocator().getConverter(propertyType).convertToString(v, Session.get().getLocale());
                            map.put(pd.getName(), new Pair(pd.getPropertyType(), s));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        propertyKeys = map;
    }

    protected Object loadProp(Pair pair) {
        return Application.get().getConverterLocator().getConverter(pair.getClazz()).convertToObject(pair.getValue(), Session.get().getLocale());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @Data
    @AllArgsConstructor
    class Pair implements Serializable {

        Class clazz;

        String value;

    }
}
