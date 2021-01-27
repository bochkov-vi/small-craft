package com.bochkov.smallcraft.wicket.web.pages.boat.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Classes;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Accessors(chain = true)
public class ChoiseEnumInput<T extends Enum> extends FormComponentPanel<T> {

    IModel<T> selected = new Model<T>();

    RadioGroup<T> group = new RadioGroup<T>("group", selected);

    @Getter
    @Setter
    boolean allowNull;

    private Class<T> enumClass;

    public ChoiseEnumInput(String id, Class<T> enumClass) {
        super(id);
        this.enumClass = enumClass;
    }

    public ChoiseEnumInput(String id, IModel<T> model, Class<T> enumClass) {
        super(id, model);
        this.enumClass = enumClass;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        List<T> values = Lists.newArrayList(enumClass.getEnumConstants());

        group.add(new ListView<T>("choice", Lists.newArrayList(Streams.concat(Stream.of(enumClass.getEnumConstants()), allowNull ? Stream.of(null) : Stream.empty()).collect(Collectors.toList()))) {
            protected void populateItem(ListItem<T> it) {
                it.add(new Radio("radio", it.getModel()));
                it.add(new Label("label", it.getModel().map(t -> getDisplayValue(t))));
                it.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        if (Objects.equals(it.getModelObject(), group.getModelObject())) {
                            oldClasses.add("active");
                        }
                        return oldClasses;
                    }
                });
            }
        });
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn-group-toggle");
                return oldClasses;
            }
        });
        add(new AttributeModifier("data-toggle", "buttons"));
        add(group);
    }

    @Override
    protected void onBeforeRender() {
        if (getModel() != null) {
            group.setModelObject(getModelObject());
        }
        super.onBeforeRender();
    }

    @Override
    public void convertInput() {
        setConvertedInput(group.getConvertedInput());
    }

    public Object getDisplayValue(T object) {
        final String value;

        String key = resourceKey(object);


        value = getString(key);

        return value;
    }

    protected String resourceKey(T object) {
        return Classes.simpleName(object.getDeclaringClass()) + '.' + object.name();
    }
}
