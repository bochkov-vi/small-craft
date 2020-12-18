package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.NotificationNumberSeqRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@MountPath("notification/edit")
public class EditPage extends CrudEditPage<Notification, Long> {

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
    public NotificationRepository getRepository() {
        return repository;
    }

    @Override
    public Notification newEntityInstance() {
        Notification notification = super.newEntityInstance();
        notification.setYear(LocalDate.now().getYear());
        notification.setDateFrom(LocalDate.now().withYear(notification.getYear()));
        notification.setDate(LocalDate.now());
        notification.setDateTo(LocalDate.now().withYear(notification.getYear()).with(TemporalAdjusters.lastDayOfYear()));
        PageParameters parameters = getPageParameters();
        Boat boat = Optional.ofNullable(parameters.get("boat").toOptionalString())
                .map(value -> getConverter(Boat.class).convertToObject(value, Session.get().getLocale())).orElse(null);
        if (boat != null) {
            notification.setBoat(boat);
            notification.setCaptain(boat.getPerson());
        }
        return notification;
    }

//    @Override
//    public Notification save(Notification entity) {
//        if (Optional.ofNullable(entity).map(Notification::getNumber).orElse(null) == null) {
//            entity.setNumber(notificationNumberSeqRepository.nextValue(entity.getYear()));
//        }
//
//        return super.save(entity);
//    }

    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<Notification> model) {
        Notification notification = model.getObject();
        super.onSave(target, model);
    }

    @Override
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Notification> model) {

    }

    @Override
    public Notification save(Notification entity) {
        return repository.safeSave(entity);
    }
}
