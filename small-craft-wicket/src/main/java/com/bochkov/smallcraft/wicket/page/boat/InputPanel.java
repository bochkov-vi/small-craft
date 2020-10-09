package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.wicket.page.person.EditPage;
import com.bochkov.smallcraft.wicket.page.person.SelectPerson;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Optional;

public class InputPanel extends GenericPanel<Boat> {

    Form<T> form = new Form<>("form");

    FormComponent<Person> person = new SelectPerson("own");

    public InputPanel(String id, IModel<Boat> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.setModel(new CompoundPropertyModel(getModel()));
        form.add(new TextField<>("id", String.class));
        form.add(new TextField<>("tailNumber", String.class).setRequired(true));
        form.add(new TextField<>("type", String.class));
        form.add(new TextField<>("model", String.class));
        person.setRequired(true);
        form.add(person);
        form.add(new AjaxLink<Void>("btn-add-own") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new EditPage() {
                    @Override
                    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
                        super.onAfterSave(target, model);
                        InputPanel.this.getModelObject().setOwn(model.getObject());
                    }
                }.setBackPage(getPage()));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(person.isVisible());
            }
        });
    }
    @Override
    protected void onConfigure() {
        person.setVisible(getModel().map(p -> !p.isNew()).orElse(false).getObject());
        super.onConfigure();
    }
}
