package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.Iterator;

public class SelectRegion extends AutoCompleteTextField<String> {

    @Inject
    NotificationRepository repository;

    public SelectRegion(String id, IModel<String> model) {
        super(id, model);
    }

    public SelectRegion(String id) {
        super(id);
    }

    @Override
    protected Iterator<String> getChoices(String input) {
        return repository.findRegionByMask(input).iterator();
    }
}
