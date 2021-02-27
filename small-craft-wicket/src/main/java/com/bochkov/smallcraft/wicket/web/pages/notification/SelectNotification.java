package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.wicket.select2.data.MaskableChoiceProvider;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import javax.inject.Inject;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class SelectNotification extends Select2Choice<Notification> {

    @Inject
    NotificationRepository repository;

    public SelectNotification(String id) {
        super(id);

    }

    public SelectNotification(String id, IModel<Notification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        setProvider(createProvider());
        getSettings().setPlaceholder(getString("notification")).setCloseOnSelect(true).setAllowClear(true).setTheme("bootstrap4");
        super.onInitialize();
    }

    ChoiceProvider<Notification> createProvider() {
        ChoiceProvider<Notification> provider = new MaskableChoiceProvider<Notification>(Notification.class,
                "boat.tailNumber", "captain.lastName", "boat.registrationNumber", "boat.model") {
            @Override
            protected Page<Notification> findAll(Specification<Notification> specification, Pageable pageable) {
                return repository.findAll(specification, pageable);
            }

            @Override
            public Path createPathForProperty(Root<Notification> root, String expression) {
                return super.createPathForProperty(root, expression);
            }
        };
        return provider;
    }
}
