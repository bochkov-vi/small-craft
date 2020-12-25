package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.wicket.component.select2.data.Maskable;
import com.bochkov.wicket.component.select2.data.MaskableChoiceProvider;
import com.google.common.collect.ImmutableList;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.Iterator;
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
            public Specification<Person> createMaskSpecification(String mask, Iterable<String> maskedPoperties) {
                Specification<Person> specification = Specification.where(null);
                for (Iterator<String> it = maskedPoperties.iterator(); it.hasNext(); ) {
                    String prop = it.next();
                    if (!"phones".equals(prop)) {
                        specification = specification.or(Maskable.maskSpecification(mask, prop));
                    } else {

                        Specification<Person> s = (r, query, cb) -> {
                            Expression maskedProperty = r.join("phones");
                            Predicate result = Maskable.stringMaskExpression(mask, maskedProperty, query, cb);
                            if (result != null) {
                                ImmutableList orders;
                                if (query.getOrderList() == null) {
                                    orders = ImmutableList.of();
                                } else {
                                    orders = ImmutableList.copyOf(query.getOrderList());
                                }

                                Expression locate = cb.locate(maskedProperty.as(String.class), (String) Optional.ofNullable(mask).orElse(""));
                                orders = ImmutableList.builder().add(new Order[]{cb.asc(locate), cb.asc(cb.length(maskedProperty.as(String.class))), cb.asc(maskedProperty)}).addAll(orders).build();
                                query.orderBy(orders);
                            }
                            return result;
                        };
                        specification = specification.or(s);
                    }
                }

                return specification;
            }
        };
        //ChoiceProvider<Person> provider = PersistableChoiceProvider.of(Person.class, (s, p) -> repository.findAll(s, p), "lastName", "legalPerson.name");
        return provider;
    }

}
