package com.bochkov.smallcraft.wicket.page.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.wicket.component.select2.data.PersistableChoiceProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

public class SelectLegalPerson extends Select2Choice<LegalPerson> {

    @SpringBean
    LegalPersonRepository repository;

    public SelectLegalPerson(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectLegalPerson(String id, IModel<LegalPerson> model) {
        super(id, model);
        setProvider(provider());
    }

    @Override
    protected void onInitialize() {
        getSettings().setPlaceholder(getString("own")).setCloseOnSelect(true).setAllowClear(true);
        super.onInitialize();
    }

    ChoiceProvider<LegalPerson> provider() {
        ChoiceProvider<LegalPerson> provider = PersistableChoiceProvider.of(LegalPerson.class, (s, p) -> repository.findAll(s, p), "name", "inn");
        return provider;
    }

}
