package com.bochkov.smallcraft.wicket.model;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.convert.IConverter;
import org.springframework.lang.NonNull;

import java.util.Optional;

public class CovertableEntityModel<T> extends LoadableDetachableModel<T> {

    String key;

    Class<T> clazz;

    public CovertableEntityModel(Class<T> clazz) {
        this.clazz = clazz;
    }

    public CovertableEntityModel(T object, Class<T> clazz) {
        super(object);
        this.clazz = clazz;
    }

    public static <T> CovertableEntityModel<T> of(@NonNull T o) {
        Class<T> c = (Class<T>) o.getClass();
        CovertableEntityModel<T> model = new CovertableEntityModel<T>(o, c);
        return model;
    }

    @Override
    protected T load() {
        return Optional.ofNullable(key).map(k -> converter().convertToObject(k, Session.get().getLocale())).orElse(null);
    }

    @Override
    protected void onDetach() {
        try {
            key = Optional.ofNullable(getObject()).map(e -> converter().convertToString(e, Session.get().getLocale())).orElse(null);
        } catch (Exception ignored) {
        }
    }

    IConverter<T> converter() {
        return Application.get().getConverterLocator().getConverter(clazz);
    }
}
