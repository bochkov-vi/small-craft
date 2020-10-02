package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.wicket.page.person.SelectPerson;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class InputPanel extends GenericPanel<Boat> {

    Form<T> form = new Form<>("form");

    public InputPanel(String id, IModel<Boat> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.setModel(new CompoundPropertyModel(getModel()));
        form.add(new TextField<>("id", String.class).setRequired(true));
        form.add(new TextField<>("type", String.class));
        form.add(new TextField<>("model", String.class));
        form.add(new SelectPerson("own"));
    }
}
