package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.wicket.component.select2.data.ConvertableChoiceProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

public class SelectPerson extends Select2Choice<Person> {

    @SpringBean
    PersonRepository repository;

    public SelectPerson(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectPerson(String id, IModel<Person> model) {
        super(id, model);
        setProvider(provider());
    }

    @Override
    protected void onInitialize() {
        getSettings().setPlaceholder(getString("person")).setCloseOnSelect(true).setAllowClear(true).setTheme("bootstrap4");
        super.onInitialize();
    }

    ChoiceProvider<Person> provider() {
        ChoiceProvider<Person> provider = new ConvertableChoiceProvider<Person>() {
            @Override
            public Page<Person> findByMask(String term, Pageable pageRequest) {
                return repository.findByMask(term, pageRequest);
            }
        };
        //ChoiceProvider<Person> provider = PersistableChoiceProvider.of(Person.class, (s, p) -> repository.findAll(s, p), "lastName", "legalPerson.name");
        return provider;
    }

}
