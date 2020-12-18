package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

public class InputPanel extends GenericPanel<Person> {


    @Inject
    PersonRepository personRepository;


    public InputPanel(String id) {
        super(id);
    }

    public InputPanel(String id, IModel<Person> model) {
        super(id, model);
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FormComponentInputPanel("inputs",getModel()));

    }

    protected void onUpdate(AjaxRequestTarget target) {

    }

}
