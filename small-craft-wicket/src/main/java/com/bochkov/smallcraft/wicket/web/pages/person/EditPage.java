package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Optional;

@MountPath("person/edit")
public class EditPage extends CrudEditPage<Person, Long> {

    @SpringBean
    PersonRepository repository;

    @SpringBean
    LegalPersonRepository legalPersonRepository;

    public EditPage(PageParameters parameters) {
        super(Person.class, parameters);
    }

    public EditPage(IModel<Person> model) {
        super(Person.class, model);
    }

    public EditPage() {
        super(Person.class);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        feedback.setEscapeModelStrings(false);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Person> model) {
        return new InputPanel(id, model) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedback);
            }
        };
    }

    @Override
    public PersonRepository getRepository() {
        return repository;
    }

    @Override
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onAfterSave(target, model);
    }

    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onSave(target, model);
    }


    @Override
    public Person newEntityInstance() {
        return new Person().setPassport(new Passport());
    }

}
