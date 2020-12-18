package com.bochkov.smallcraft.wicket.web.pages.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("person/legal/edit")
public class EditPage extends CrudEditPage<LegalPerson, Long> {

    @SpringBean
    LegalPersonRepository repository;

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
    public LegalPersonRepository getRepository() {
        return repository;
    }

}
