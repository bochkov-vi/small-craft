package com.bochkov.smallcraft.wicket.page.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("person/legal/edit")
public class EditPage extends CrudEditPage<LegalPerson, Long> {

    @SpringBean
    PersonRepository repository;

    public EditPage(PageParameters parameters) {
        super(LegalPerson.class, parameters);
    }

    public EditPage(IModel<LegalPerson> model) {
        super(LegalPerson.class, model);
    }

    public EditPage() {
        super(LegalPerson.class);
    }

    @Override
    protected Component createInputPanel(String id, IModel<LegalPerson> model) {
        return new InputPanel(id, model);
    }

    @Override
    public PersonRepository getJpaRepository() {
        return repository;
    }

}
