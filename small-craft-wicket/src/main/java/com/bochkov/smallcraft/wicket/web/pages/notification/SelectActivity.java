package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.pages.person.component.AbstractPageableSelect2MultiString;
import com.google.common.base.Splitter;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class SelectActivity extends AbstractPageableSelect2MultiString {

    @Inject
    NotificationRepository repository;

    public SelectActivity(String id) {
        super(id);
        setAddQueryToResult(true);
    }

    public SelectActivity(String id, IModel<Collection<String>> model) {
        super(id, (IModel<Collection<String>>) model);
        setAddQueryToResult(true);
    }

    @Override
    public Page<String> query(String term, Pageable pageable) {
        return repository.findActivityByMask(term, pageable);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setAddQueryToResult(true);
    }


}
