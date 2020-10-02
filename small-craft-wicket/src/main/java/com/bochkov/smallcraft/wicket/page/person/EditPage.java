package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("person/edit")
public class EditPage extends CrudEditPage<Person, Long> {

    @SpringBean
    PersonRepository repository;

    public EditPage(PageParameters parameters) {
        super(parameters);
    }

    public EditPage(IModel<Person> model) {
        super(model);
    }

    public EditPage() {
        super(Person.class);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Person> model) {
        return new InputPanel(id, model);
    }

    @Override
    public PersonRepository getJpaRepository() {
        return repository;
    }

    @Override
    public Class<Person> getEntityClass() {
        return Person.class;
    }

    @Override
    public Person newEntityInstance() {
        return new Person().setPassport(new Passport());
    }
}
