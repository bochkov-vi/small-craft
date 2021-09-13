package com.bochkov.smallcraft.wicket.web.pages.person.component;

import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;

public class Select2FirstName extends AbstractPageableSelect2String {

    @Inject
    PersonRepository personRepository;

    public Select2FirstName(String id) {
        super(id);
    }

    public Select2FirstName(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    public Page<String> query(String term, Pageable pageable) {
         return personRepository.findFirstNameByMask(term, pageable);
    }
}
