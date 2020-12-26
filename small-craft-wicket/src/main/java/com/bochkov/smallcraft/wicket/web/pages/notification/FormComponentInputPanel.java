package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.Html5AttributesBehavior;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.legalPerson.FormComponentInput;
import com.bochkov.smallcraft.wicket.web.pages.unit.SelectUnit;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.collect.Sets;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

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

    FormComponent<Unit> unit = new SelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<Notification> id = new HiddenField<>("id", PersistableModel.of(ntpk -> notificationRepository.findById(ntpk)), Notification.class);

    FormComponent<Integer> year = new TextField<>("year", Model.of(), Integer.class);

    FormComponent<Integer> number = new TextField<>("number", Model.of(), Integer.class);

    FormComponent<Collection<String>> region = new SelectRegion("region", new CollectionModel<String>());

    FormComponent<Person> captain = new CaptainPanel("captain", PersistableModel.of(id -> personRepository.findById(id))).setCanSelect(true);

    FormComponent<Boat> boat = new com.bochkov.smallcraft.wicket.web.pages.boat.FormComponentInputPanel("boat", PersistableModel.of(id -> boatRepository.findById(id))) {
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
        FormComponentErrorBehavior.append(this);
        captain.setOutputMarkupId(true);
        setOutputMarkupId(true);
        add(region, captain, boat, legalPerson, date, dateFrom, dateTo, activities, timeOfDay, tck, id, number, year, unit);
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
        entity.setRegion(Optional.ofNullable(region.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        entity.setTck(tck.getConvertedInput());
        entity.setTimeOfDay(timeOfDay.getConvertedInput());
        entity.setUnit(unit.getConvertedInput());
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
        region.setModelObject(Optional.ofNullable(e.getRegion()).map(Sets::newHashSet).orElse(null));
        tck.setModelObject(e.getTck());
        timeOfDay.setModelObject(e.getTimeOfDay());
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
