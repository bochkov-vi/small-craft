package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.NotificationPK;
import com.bochkov.smallcraft.jpa.repository.NotificationNumberSeqRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@MountPath("notification/edit")
public class EditPage extends CrudEditPage<Notification, NotificationPK> {

    @SpringBean
    NotificationRepository repository;

    @SpringBean
    NotificationNumberSeqRepository notificationNumberSeqRepository;

    public EditPage(PageParameters parameters) {
        super(Notification.class, parameters);
    }

    public EditPage(IModel<Notification> model) {
        super(Notification.class, model);
    }

    public EditPage() {
        super(Notification.class);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Notification> model) {
        return new InputPanel(id, model);
    }

    @Override
    public NotificationRepository getJpaRepository() {
        return repository;
    }

    @Override
    public Notification newEntityInstance() {
        Notification notification = super.newEntityInstance().setId(new NotificationPK(LocalDate.now().getYear(), null));
        notification.setDateFrom(LocalDate.now().withYear(notification.getId().getYear()));
        notification.setDate(LocalDate.now());
        notification.setDateTo(LocalDate.now().withYear(notification.getId().getYear()).with(TemporalAdjusters.lastDayOfYear()));
        return notification;
    }

    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<Notification> model) {
        Notification notification = model.getObject();
        if (notification != null) {
            NotificationPK pk = notification.getId();
            if (pk == null) {
                pk = new NotificationPK(LocalDate.now().getYear(), null);
                notification.setId(pk);
            } else {
                pk = notification.getId();
            }
            if (pk.getYear() == null) {
                pk.setYear(LocalDate.MIN.getYear());
            }
            Integer year = pk.getYear();
            if (pk.getNumber() == null) {
                pk.setNumber(notificationNumberSeqRepository.nextValue(year));
            }
        }
        super.onSave(target, model);
    }
}
