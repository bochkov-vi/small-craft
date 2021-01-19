package com.bochkov.smallcraft.wicket.web.pages.boat.component;

import com.google.common.collect.Lists;
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


public class ChoiseEnumInput<T extends Enum> extends FormComponentPanel<T> {

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
        IModel<T> selected = new Model<T>();
        RadioGroup<T> group = new RadioGroup<T>("group", selected);
        group.add(new ListView<T>("choice", Lists.newArrayList(enumClass.getEnumConstants())) {
            protected void populateItem(ListItem<T> it) {
                it.add(new Radio("radio", it.getModel()));
                it.add(new Label("label", it.getModel().map(t -> getDisplayValue(t))));
            }
        });
        add(group);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    @Override
    public void convertInput() {
        super.convertInput();
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
