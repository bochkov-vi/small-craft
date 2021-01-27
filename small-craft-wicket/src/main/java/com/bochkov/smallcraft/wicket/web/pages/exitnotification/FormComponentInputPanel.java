package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.LocalDateTimeTextFieldCalendar;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.boat.SelectPier;
import com.bochkov.smallcraft.wicket.web.pages.notification.SelectActivity;
import com.bochkov.smallcraft.wicket.web.pages.notification.SelectNotification;
import com.bochkov.smallcraft.wicket.web.pages.notification.SelectRegion;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.experimental.Accessors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
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
public class FormComponentInputPanel extends CompositeInputPanel<ExitNotification> {

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

    FormComponent<LocalDateTime> estimatedReturnDateTime = new LocalDateTimeTextFieldCalendar("estimatedReturnDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<LocalDateTime> exitDateTime = new LocalDateTimeTextFieldCalendar("exitDateTime", Model.of(), "dd.MM.yyyy HH:mm").setRequired(true);

    FormComponent<LocalDateTime> returnDateTime = new LocalDateTimeTextFieldCalendar("returnDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<LocalDateTime> returnCallDateTime = new LocalDateTimeTextFieldCalendar("returnCallDateTime", Model.of(), "dd.MM.yyyy HH:mm");

    FormComponent<Collection<String>> regions = new SelectRegion("regions", new CollectionModel<>());

    FormComponent<Unit> unit = new SessionSelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    FormComponent<Collection<String>> activities = new SelectActivity("activities", new CollectionModel<>());

    FormComponent<Boat> boat = new com.bochkov.smallcraft.wicket.web.pages.boat.FormComponentInputPanel("boat", PersistableModel.of(id -> boatRepository.findById(id))) {
        public void onUpdate(AjaxRequestTarget target) {
            Optional<Boat> b = Optional.ofNullable(getModelObject());
            if (b.isPresent()) {
                if (Strings.isNullOrEmpty(pier.getModelObject())) {
                    b.map(Boat::getPier).ifPresent(p -> pier.setModelObject(p));
                    target.add(pier);
                }
                if (!regions.getModel().isPresent().getObject() || regions.getModel().map(Collection::isEmpty).getObject()) {
                    Optional<Notification> n = notificationRepository.findTopByBoatOrderByNumberDesc(b.get());
                    n.map(Notification::getRegions).map(Sets::newHashSet).ifPresent(rg -> regions.setModelObject(rg));
                    target.add(regions);
                }
            }
        }
    }.setCanSelect(true);

    IModel<Boolean> captainEqOwner = Model.of(true);

    FormComponent<Person> captain = new com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel("captain", PersistableModel.of(id -> personRepository.findById(id))) {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!captainEqOwner.getObject()).setEnabled(!captainEqOwner.getObject());
        }
    }.setCanSelect(true);

    FormComponent<Notification> notification = new SelectNotification("notification", PersistableModel.of(id -> notificationRepository.findById(id))).setRequired(true);

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
        e.setRegions(Optional.ofNullable(regions.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        e.setReturnCallDateTime(returnCallDateTime.getConvertedInput());
        e.setReturnDateTime(returnDateTime.getConvertedInput());
        e.setActivities(activities.getConvertedInput() != null ? Sets.newHashSet(activities.getConvertedInput()) : null);
        if (captainEqOwner.getObject()) {
            Person cap = Optional.ofNullable(boat.getConvertedInput()).map(Boat::getPerson).orElse(null);
            if (cap != null) {
                e.setCaptain(cap);
            }
        } else {
            e.setCaptain(captain.getConvertedInput());
        }
        e.setEstimatedReturnDateTime(estimatedReturnDateTime.getConvertedInput());
        setConvertedInput(e);
    }

    @Override
    protected void initBeforeRenderer() {
        id.setModelObject(getModelObject());
        boat.setModelObject(getModel().map(ExitNotification::getBoat).orElse(null).getObject());
        unit.setModelObject(getModel().map(ExitNotification::getUnit).getObject());
        captain.setModelObject(getModel().map(ExitNotification::getCaptain).orElse(null).getObject());
        exitCallDateTime.setModelObject(getModel().map(ExitNotification::getExitCallDateTime).orElse(null).getObject());
        exitDateTime.setModelObject(getModel().map(ExitNotification::getExitDateTime).orElse(null).getObject());
        notification.setModelObject(getModel().map(ExitNotification::getNotification).orElse(null).getObject());
        pier.setModelObject(getModel().map(ExitNotification::getPier).orElse(null).getObject());
        regions.setModelObject(getModel().map(ExitNotification::getRegions).map(Sets::newHashSet).orElseGet(Sets::newHashSet).getObject());
        activities.setModelObject(getModel().map(ExitNotification::getActivities).map(Sets::newHashSet).orElseGet(Sets::newHashSet).getObject());
        returnCallDateTime.setModelObject(getModel().map(ExitNotification::getReturnCallDateTime).orElse(null).getObject());
        returnDateTime.setModelObject(getModel().map(ExitNotification::getReturnDateTime).orElse(null).getObject());
        estimatedReturnDateTime.setModelObject(getModel().map(ExitNotification::getEstimatedReturnDateTime).orElse(null).getObject());
    }

    @Override
    protected void onConfigure() {

        super.onConfigure();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        notification.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getModelObject().putData(notification.getModelObject());
                target.add(FormComponentInputPanel.this);
            }
        });
        pier.setOutputMarkupId(true);
        regions.setOutputMarkupId(true);
        setOutputMarkupId(true);
        Optional<ExitNotification> entity = Optional.ofNullable(getModelObject());
        captainEqOwner.setObject(Objects.equals(entity.map(ExitNotification::getBoat).map(Boat::getPerson).orElse(null), entity.map(ExitNotification::getCaptain).orElse(null)));
        add(id, estimatedReturnDateTime, exitCallDateTime, exitDateTime, returnCallDateTime, returnDateTime, pier, regions, boat, unit, activities, notification);
        WebMarkupContainer captainConteiner = new WebMarkupContainer("captain-container");
        add(captainConteiner.setOutputMarkupId(true));
        captainConteiner.add(new AjaxLink<Boolean>("btn-captain-eq-owner", captainEqOwner) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                captainEqOwner.setObject(!captainEqOwner.getObject());
                target.add(captainConteiner);
            }
        }.add(new Label("btn-captain-eq-owner-label", new StringResourceModel("btn-captain-eq-owner.${captainEqOwner}", Model.of(this)).setParameters(captainEqOwner.getObject()))));


        captainConteiner.add(captain);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
