package com.bochkov.smallcraft.wicket.page.store.exitnotification;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.LocalDateTimeTextFieldCalendar;
import com.bochkov.smallcraft.wicket.page.store.boat.SelectPier;
import com.bochkov.smallcraft.wicket.page.store.notification.SelectRegion;
import com.bochkov.smallcraft.wicket.page.store.unit.SelectUnit;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.experimental.Accessors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Accessors(chain = true)
public class FormComponentInputPanel extends FormComponentPanel<ExitNotification> {

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    BoatRepository boatRepository;

    @Inject
    UnitRepository unitRepository;

    FormComponent<ExitNotification> id = new HiddenField<ExitNotification>("id", PersistableModel.of(id -> exitNotificationRepository.findById(id)), ExitNotification.class);

    FormComponent<LocalDateTime> exitCallDateTime = new LocalDateTimeTextFieldCalendar("exitCallDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<LocalDateTime> exitDateTime = new LocalDateTimeTextFieldCalendar("exitDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<LocalDateTime> returnDateTime = new LocalDateTimeTextFieldCalendar("returnDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<LocalDateTime> returnCallDateTime = new LocalDateTimeTextFieldCalendar("returnCallDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<Collection<String>> region = new SelectRegion("region", new CollectionModel<>());

    FormComponent<Unit> unit = new SelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    FormComponent<Boat> boat = new com.bochkov.smallcraft.wicket.page.store.boat.FormComponentInputPanel("boat", PersistableModel.of(id -> boatRepository.findById(id))) {
        public void onUpdate(AjaxRequestTarget target) {
            Optional<Boat> b = Optional.ofNullable(getModelObject());
            if (b.isPresent()) {
                if (Strings.isNullOrEmpty(pier.getModelObject())) {
                    b.map(Boat::getPier).ifPresent(p -> pier.setModelObject(p));
                    target.add(pier);
                }
                if (!region.getModel().isPresent().getObject() || region.getModel().map(Collection::isEmpty).getObject()) {
                    Optional<Notification> n = notificationRepository.findTopByBoatOrderByNumberDesc(b.get());
                    n.map(Notification::getRegion).map(Sets::newHashSet).ifPresent(rg -> region.setModelObject(rg));
                    target.add(region);
                }
            }
        }
    }.setCanSelect(true);

    FormComponent<Notification> notification;

    FormComponent<Person> captain = new com.bochkov.smallcraft.wicket.page.store.person.FormComponentInputPanel("captain", PersistableModel.of(id -> personRepository.findById(id))).setCanSelect(true);

    IModel<Boolean> captainEqOwner = Model.of(true);

    public FormComponentInputPanel(String id) {
        super(id);
    }

    public FormComponentInputPanel(String id, IModel<ExitNotification> model) {
        super(id, model);
    }

    public Boolean getCaptainEqOwner() {
        return captainEqOwner.getObject();
    }

    @Override
    public void convertInput() {
        ExitNotification e = id.getConvertedInput();
        if (e == null) {
            e = new ExitNotification();
        }
        e.setBoat(boat.getConvertedInput());

        e.setExitCallDateTime(exitCallDateTime.getConvertedInput());
        e.setExitDateTime(exitDateTime.getConvertedInput());
//        e.setNotification(notification.getConvertedInput());
        e.setUnit(unit.getConvertedInput());
        e.setPier(pier.getConvertedInput());
        e.setRegion(Optional.ofNullable(region.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        e.setReturnCallDateTime(returnCallDateTime.getConvertedInput());
        e.setReturnDateTime(returnDateTime.getConvertedInput());
        if (captainEqOwner.getObject()) {
            Person cap = Optional.ofNullable(boat.getConvertedInput()).map(Boat::getPerson).orElse(null);
            if (cap != null) {
                e.setCaptain(cap);
            }
        } else {
            e.setCaptain(captain.getConvertedInput());
        }
        setConvertedInput(e);
    }

    @Override
    protected void onBeforeRender() {
        id.setModelObject(getModelObject());
        boat.setModelObject(getModel().map(ExitNotification::getBoat).orElse(null).getObject());
        unit.setModelObject(getModel().map(ExitNotification::getUnit).getObject());
        captain.setModelObject(getModel().map(ExitNotification::getCaptain).orElse(null).getObject());
        exitCallDateTime.setModelObject(getModel().map(ExitNotification::getExitCallDateTime).orElse(null).getObject());
        exitDateTime.setModelObject(getModel().map(ExitNotification::getExitDateTime).orElse(null).getObject());
//        notification.setModelObject(getModel().map(ExitNotification::getNotification).orElse(null).getObject());
        pier.setModelObject(getModel().map(ExitNotification::getPier).orElse(null).getObject());
        region.setModelObject(getModel().map(ExitNotification::getRegion).map(Sets::newHashSet).orElse(null).getObject());
        returnCallDateTime.setModelObject(getModel().map(ExitNotification::getReturnCallDateTime).orElse(null).getObject());
        returnDateTime.setModelObject(getModel().map(ExitNotification::getReturnDateTime).orElse(null).getObject());
        super.onBeforeRender();
    }

    @Override
    protected void onConfigure() {
        captain.setVisible(!captainEqOwner.getObject()).setEnabled(!captainEqOwner.getObject());
        super.onConfigure();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        pier.setOutputMarkupId(true);
        region.setOutputMarkupId(true);
        setOutputMarkupId(true);
        Optional<ExitNotification> entity = Optional.ofNullable(getModelObject());
        captainEqOwner.setObject(Objects.equals(entity.map(ExitNotification::getBoat).map(Boat::getPerson).orElse(null), entity.map(ExitNotification::getCaptain).orElse(null)));
        add(id, exitCallDateTime, exitDateTime, returnCallDateTime, returnDateTime, pier, region, boat, captain, unit);
        add(new AjaxLink<Boolean>("btn-captain-eq-owner", captainEqOwner) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                captainEqOwner.setObject(!captainEqOwner.getObject());
                target.add(FormComponentInputPanel.this);
            }
        }.add(new Label("btn-captain-eq-owner-label", new StringResourceModel("btn-captain-eq-owner.${captainEqOwner}", Model.of(this)).setParameters(captainEqOwner.getObject()))));

    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
