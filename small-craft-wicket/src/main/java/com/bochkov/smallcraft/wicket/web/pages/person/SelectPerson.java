package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.wicket.select2.data.MaskableChoiceProvider;
import com.google.common.base.Joiner;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Optional;

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
        ChoiceProvider<Person> provider = new MaskableChoiceProvider<Person>(Person.class, "lastName", "phones", "email") {
            @Override
            protected Page findAll(Specification<Person> specification, Pageable pageable) {
                return repository.findAll(specification, pageable);
            }

            @Override
            public Path createPathForProperty(Root<Person> root, String expression) {
               /* if ("phones".equals(expression)) {
                    Path maskedProperty = root.join("phones", JoinType.LEFT);
                    return maskedProperty;
                }*/
                return super.createPathForProperty(root, expression);
            }

            @Override
            public String getDisplayValue(Person object) {
                return Optional.ofNullable(object).map(person -> Joiner.on(" ").skipNulls().join(person.getLastName(), person.getFirstName(), person.getMiddleName())).orElse(null);
            }
        };
        //ChoiceProvider<Person> provider = PersistableChoiceProvider.of(Person.class, (s, p) -> repository.findAll(s, p), "lastName", "legalPerson.name");
        return provider;
    }

}
