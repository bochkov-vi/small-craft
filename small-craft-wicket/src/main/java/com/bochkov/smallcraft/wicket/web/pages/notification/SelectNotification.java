package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.wicket.select2.data.MaskableChoiceProvider;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.Optional;

public class SelectNotification extends Select2Choice<Notification> {

    @Inject
    NotificationRepository repository;

    Boolean onlyActive = true;

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
                "number","boat.tailNumber", "captain.lastName", "boat.registrationNumber", "boat.model") {
            @Override
            protected Page<Notification> findAll(Specification<Notification> specification, Pageable pageable) {
                specification = specification.and(Optional.ofNullable(onlyActive).map(aboolean->(Specification<Notification>) (r, q, b) -> b.greaterThanOrEqualTo(r.get("dateTo"), LocalDate.now())).orElse(null));
                return repository.findAll(specification, pageable);
            }
        };
        return provider;
    }
}
