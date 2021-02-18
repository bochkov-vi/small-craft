package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.boat.SelectPier;
import com.bochkov.smallcraft.wicket.web.pages.legalPerson.FormComponentInput;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class FormComponentInputPanel extends CompositeInputPanel<Notification> {

    @Inject
    PersonRepository personRepository;

    @Inject
    BoatRepository boatRepository;

    @Inject
    LegalPersonRepository legalPersonRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    UnitRepository unitRepository;

    FormComponent<Unit> unit = new SessionSelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<Notification> id = new HiddenField<>("id", PersistableModel.of(ntpk -> notificationRepository.findById(ntpk)), Notification.class);

    FormComponent<Integer> year = new TextField<>("year", Model.of(), Integer.class);

    FormComponent<Integer> number = new TextField<>("number", Model.of(), Integer.class);

    FormComponent<Collection<String>> regions = new SelectRegion("regions", new CollectionModel<String>());

    FormComponent<Person> captain = new CaptainPanel("captain", PersistableModel.of(id -> personRepository.findById(id))).setCanSelect(true);

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    com.bochkov.smallcraft.wicket.web.pages.boat.FormComponentInputPanel boat = new com.bochkov.smallcraft.wicket.web.pages.boat.FormComponentInputPanel("boat", PersistableModel.of(id -> boatRepository.findById(id))) {
        @Override
        public void onUpdate(AjaxRequestTarget target) {
            Boat b = getModelObject();
            if (b != null) {
                if (captain.getModelObject() == null && captain.isEnabledInHierarchy() && captain.isVisibleInHierarchy()) {
                    captain.setModelObject(Optional.ofNullable(b.getPerson()).orElse(null));
                    target.add(captain);
                }
            }

        }
    }.setCanSelect(true);

    FormComponent<LegalPerson> legalPerson = new FormComponentInput("legalPerson", PersistableModel.of(id -> legalPersonRepository.findById(id))).setCanSelect(true);

    FormComponent<LocalDate> date = new LocalDateTextField("date", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateFrom = new LocalDateTextField("dateFrom", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateTo = new LocalDateTextField("dateTo", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<Collection<String>> activities = new SelectActivity("activities", new CollectionModel<>());

    FormComponent<String> timeOfDay = new SelectTimeOfDay("timeOfDay", Model.of());

    FormComponent<Boolean> tck = new CheckBox("tck", Model.of());

    FormComponent<Boolean> voiceCall = new CheckBox("voiceCall", Model.of());

    IModel<Boolean> captainEqOwner = Model.of(true);

    public FormComponentInputPanel(String id) {
        super(id);
    }

    public FormComponentInputPanel(String id, IModel<Notification> model) {
        super(id, model);
    }

    public Boolean getCaptainEqOwner() {
        return captainEqOwner.getObject();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        /*voiceCall.add(new IValidator<Boolean>() {
            @Override
            public void validate(IValidatable<Boolean> validatable) {
                if (validatable.getModel().orElse(false).getObject()) {
                    regions.setRequired(true);
                    activities.setRequired(true);
                    boat.getPier().setRequired(true);
                } else {
                    regions.setRequired(false);
                    activities.setRequired(false);
                    boat.getPier().setRequired(false);
                }
            }
        });*/
        add(new IValidator<Notification>() {
            @Override
            public void validate(IValidatable<Notification> validatable) {
                Notification notification = validatable.getValue();
                Optional<Notification> optional = Optional.ofNullable(notification);
                if (optional.isPresent()) {
                    if (optional.map(Notification::getCanVoiceCall).orElse(false)) {
                        if (optional.map(Notification::getPier).map(Strings::isNullOrEmpty).orElse(true)) {
                            pier.error(new ValidationError().addKey("Required"));
                        }
                        if (optional.map(Notification::getActivities).map(Set::isEmpty).orElse(true)) {
                            activities.error(new ValidationError().addKey("Required"));
                        }
                        if (optional.map(Notification::getRegions).map(Set::isEmpty).orElse(true)) {
                            regions.error(new ValidationError().addKey("Required"));
                        }
                        if (boat.hasErrorMessage() || activities.hasErrorMessage() || regions.hasErrorMessage()) {
                            validatable.error(new ValidationError().addKey("canVoiceCallWithNoData"));
                        }
                    }
                }
            }
        });
        pier.add(new PatternValidator("[^-]+"));

        captain.setOutputMarkupId(true);
        setOutputMarkupId(true);
        add(voiceCall,pier, regions, captain, boat, legalPerson, date, dateFrom, dateTo, activities, timeOfDay, tck, id, number, year, unit);
        legalPerson.setVisible(false).setEnabled(false);
        tck.setOutputMarkupId(true);
        add(new AjaxLink<Boolean>("btn-captain-eq-owner", captainEqOwner) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                captainEqOwner.setObject(!captainEqOwner.getObject());
                target.add(FormComponentInputPanel.this);
            }
        }.add(new Label("btn-captain-eq-owner-label", new StringResourceModel("btn-captain-eq-owner.${captainEqOwner}", Model.of(this)).setParameters(captainEqOwner.getObject()))));
        dateFrom.setOutputMarkupId(true);
        date.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dateFrom.setModelObject(date.getModelObject());
                target.add(dateFrom);
            }
        });
        FormComponentErrorBehavior.append(this);
    }

    @Override
    public void convertInput() {

        Notification entity = id.getConvertedInput();
        if (entity == null) {
            entity = new Notification();
        }
        entity.setYear(year.getConvertedInput());
        entity.setNumber(number.getConvertedInput());
        entity.setDate(date.getConvertedInput());

        entity.setBoat(boat.getConvertedInput());
        if (captainEqOwner.getObject()) {
            entity.setCaptain(Optional.ofNullable(boat.getConvertedInput()).map(Boat::getPerson).orElse(null));
        } else {
            entity.setCaptain(captain.getConvertedInput());
        }
        entity.setDateTo(dateTo.getConvertedInput());
        entity.setDateFrom(dateFrom.getConvertedInput());
        entity.setActivities(Optional.ofNullable(activities.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        entity.setRegions(Optional.ofNullable(regions.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        entity.setTck(tck.getConvertedInput());
        entity.setTimeOfDay(timeOfDay.getConvertedInput());
        entity.setUnit(unit.getConvertedInput());
        entity.setCanVoiceCall(voiceCall.getConvertedInput());
        entity.setPier(pier.getConvertedInput());
        setConvertedInput(entity);
    }

    @Override
    protected void initBeforeRenderer() {

        Notification e = getModelObject();
        id.setModelObject(e);
        unit.setModelObject(e.getUnit());
        year.setModelObject(e.getYear());
        number.setModelObject(e.getNumber());
        date.setModelObject(e.getDate());
        captain.setModelObject(e.getCaptain());
        boat.setModelObject(e.getBoat());
        dateTo.setModelObject(e.getDateTo());
        dateFrom.setModelObject(e.getDateFrom());
        activities.setModelObject(Optional.ofNullable(e.getActivities()).map(Sets::newHashSet).orElse(null));
        regions.setModelObject(Optional.ofNullable(e.getRegions()).map(Sets::newHashSet).orElse(null));
        tck.setModelObject(e.getTck());
        voiceCall.setModelObject(e.getCanVoiceCall());
        timeOfDay.setModelObject(e.getTimeOfDay());
        pier.setModelObject(e.getPier());

    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onConfigure() {

        if (captainEqOwner.getObject()) {
            captain.setEnabled(false);
            captain.setVisible(false);
        } else {
            captain.setEnabled(true);
            captain.setVisible(true);
        }
        super.onConfigure();
    }
}
