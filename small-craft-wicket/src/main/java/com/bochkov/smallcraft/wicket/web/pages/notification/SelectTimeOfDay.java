package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.pages.person.component.AbstractPageableSelect2String;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;

public class SelectTimeOfDay extends AbstractPageableSelect2String {

    @Inject
    NotificationRepository repository;

    public SelectTimeOfDay(String id) {
        super(id);
        setAddQueryToResult(true);
    }

    public SelectTimeOfDay(String id, IModel<String> model) {
        super(id, model);
        setAddQueryToResult(true);
    }

    @Override
    public Page<String> query(String term, Pageable pageable) {
        return repository.findTimeOfDayByMask(term, pageable);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setAddQueryToResult(true);
    }
}
