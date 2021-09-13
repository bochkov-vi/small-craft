package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.web.pages.person.component.AbstractPageableSelect2String;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;

public class SelectType extends AbstractPageableSelect2String {

    @Inject
    BoatRepository repository;

    public SelectType(String id) {
        super(id);
        setAddQueryToResult(true);
    }

    public SelectType(String id, IModel<String> model) {
        super(id, model);
        setAddQueryToResult(true);
    }
    @Override
    protected void onInitialize() {
        getSettings().setPlaceholder(getString("type")).setCloseOnSelect(true).setAllowClear(true).setTheme("bootstrap4");
        super.onInitialize();
    }
    @Override
    public Page<String> query(String term, Pageable pageable) {
        return repository.findTypeByMask(term, pageable);
    }


}
