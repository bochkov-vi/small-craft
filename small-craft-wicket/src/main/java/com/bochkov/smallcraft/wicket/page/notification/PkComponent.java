package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.entity.NotificationPK;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.IModel;

public class PkComponent extends FormComponentPanel<NotificationPK> {

    FormComponent<Integer> year = new NumberTextField<>("id.year");

    FormComponent<Integer> number = new NumberTextField<>("id.number");

    public PkComponent(String id) {
        super(id);
    }

    public PkComponent(String id, IModel model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(year, number);
    }

    @Override
    public void convertInput() {
        setConvertedInput(new NotificationPK(year.getConvertedInput(), number.getConvertedInput()));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        year.setModelObject(getModel().map(NotificationPK::getYear).getObject());
        number.setModelObject(getModel().map(NotificationPK::getNumber).getObject());
    }
}
