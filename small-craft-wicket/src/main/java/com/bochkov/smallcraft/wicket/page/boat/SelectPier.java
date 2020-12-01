package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.page.person.component.AbstractPageableSelect2String;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;

public class SelectPier extends AbstractPageableSelect2String {

    @Inject
    BoatRepository repository;

    public SelectPier(String id) {
        super(id);
        setAddQueryToResult(true);
    }

    public SelectPier(String id, IModel<String> model) {
        super(id, model);
        setAddQueryToResult(true);
    }

    @Override
    public Page<String> query(String term, Pageable pageable) {
        return repository.findPierByMask(term, pageable);
    }


}
