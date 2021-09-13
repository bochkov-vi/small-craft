package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.pages.person.component.AbstractPageableSelect2MultiString;
import com.google.common.base.Splitter;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;

public class SelectRegion extends AbstractPageableSelect2MultiString {

    @Inject
    NotificationRepository repository;

    public SelectRegion(String id) {
        super(id);
        setAddQueryToResult(true);
    }

    public SelectRegion(String id, IModel<Collection<String>> model) {
        super(id, model);
        setAddQueryToResult(true);
    }

    @Override
    public Page<String> query(String term, Pageable pageable) {
        return repository.findRegionByMask(term, pageable);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setAddQueryToResult(true);
    }
}
