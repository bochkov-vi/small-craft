package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.OnChangeDuplicateBehavior;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.crud.CrudPage;
import com.bochkov.smallcraft.wicket.web.pages.boat.SelectPier;
import com.bochkov.smallcraft.wicket.web.pages.legalPerson.FormComponentInput;
import com.bochkov.smallcraft.wicket.web.pages.person.EditPage;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.springframework.data.jpa.domain.Specification;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    FormComponent<Person> captain = new CaptainPanel("captain", PersistableModel.of(id -> personRepository.findById(id)), boat.getModel().map(Boat::getPerson));

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    FormComponent<LegalPerson> legalPerson = new FormComponentInput("legalPerson", PersistableModel.of(id -> legalPersonRepository.findById(id))).setCanSelect(true);

    FormComponent<LocalDate> date = new LocalDateTextField("date", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateFrom = new LocalDateTextField("dateFrom", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateTo = new LocalDateTextField("dateTo", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<Collection<String>> activities = new SelectActivity("activities", new CollectionModel<>());

    FormComponent<String> timeOfDay = new SelectTimeOfDay("timeOfDay", Model.of());

    FormComponent<Boolean> tck = new CheckBox("tck", Model.of());

    FormComponent<Boolean> voiceCall = new CheckBox("voiceCall", Model.of());

    //IModel<Boolean> captainEqOwner = Model.of(true);

    public FormComponentInputPanel(String id) {
        super(id);
    }

    public FormComponentInputPanel(String id, IModel<Notification> model) {
        super(id, model);
    }


    @Override
    protected void onInitialize() {
        number.setEnabled(false);
        boat.setOnPersonEdit((personModel, target) -> {
            EditPage personEditPage = new EditPage(personModel);
            personEditPage.addOnBack((editedPerson) -> {
                Boat boatEntity = boat.getModelObject();
                if (boatEntity != null) {
                    boatEntity.setPerson(editedPerson.getObject());
                }
            });
            setResponsePage(personEditPage);
            personEditPage.setBackPage(getPage());
        });
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer("content");
        add(container);
        container.setOutputMarkupId(true);
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
                        if (boat.hasErrorMessage() || activities.hasErrorMessage() || regions.hasErrorMessage() || pier.hasErrorMessage()) {
                            validatable.error(new ValidationError().addKey("canVoiceCallWithNoData"));
                        }

                    }
                }
            }
        });
        add(new IValidator<Notification>() {
            @Override
            public void validate(IValidatable<Notification> validatable) {
                Notification notification = validatable.getValue();
                if (notification != null) {
                    Boat boat = notification.getBoat();
                    if (boat != null) {
                        LocalDate d1 = notification.getDateFrom();
                        if (d1 != null) {
                            LocalDate d2 = notification.getDateTo();
                            Specification<Notification> specification = (r, q, b) -> {
                                Predicate predicate = b.and(b.equal(r.get("boat"), boat),
                                        b.lessThan(r.get("dateFrom"), d2 != null ? d2 : LocalDate.now()),
                                        b.greaterThan(b.coalesce(r.get("dateTo"), LocalDate.now()), d1));
                                return predicate;
                            };
                            List<Notification> duplicates = notificationRepository.findAll(specification);
                            if (!duplicates.isEmpty()) {
                                for (Notification exit1 : duplicates) {
                                    PageParameters parameters = CrudPage.pageParameters(exit1);
                                    Behavior behavior = new AbstractAjaxBehavior() {
                                        @Override
                                        public void onRequest() {
                                            CrudPage currentPage = findParent(CrudPage.class);
                                            com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage(parameters);
                                            editPage.setBackPage(currentPage);
                                            setResponsePage(editPage);
                                        }

                                        @Override
                                        public boolean rendersPage() {
                                            return false;
                                        }
                                    };
                                    add(behavior);
                                    CharSequence url = urlForListener(behavior, null);
                                    error(String.format("Найдено пересечение периодов деятельности: <a href='%1$s'>%2$s<a>", url, exit1.toString()));
                                }
                            }
                        }
                    }
                }

            }
        });


        pier.add(new PatternValidator("[^-]+"));

        setOutputMarkupId(true);
        queue(voiceCall, pier, regions, captain, boat, legalPerson, date, dateFrom, dateTo, activities, timeOfDay, tck, id, number, year, unit);
        legalPerson.setVisible(false).setEnabled(false);
        tck.setOutputMarkupId(true);
        dateFrom.setOutputMarkupId(true);
        date.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dateFrom.setModelObject(date.getModelObject());
                target.add(dateFrom);
            }
        });
        add(new IValidator<Notification>() {
            @Override
            public void validate(IValidatable<Notification> validatable) {
                Notification notification = validatable.getValue();
                if (notification != null) {
                    if (notification.getDateFrom() != null) {
                        if (notification.getDateTo() != null && !notification.getDateTo().isAfter(notification.getDateFrom())) {
                            validatable.error(new ValidationError("Не допустимый период действия уведомления"));
                            dateFrom.error(new ValidationError());
                            dateTo.error(new ValidationError());
                        }
                    }
                }
            }
        });
        number.add(new OnChangeDuplicateBehavior<Integer, Notification>(getModel(), Notification.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Notification entity) {
                target.add(container);
                setModelObject(entity);
            }

            @Override
            public List<Notification> findDuplicates(Integer number) {
                return notificationRepository.findByYearAndNumber(year.getModelObject(), number);
            }
        });
        //regions.add(new SplitByComaValidator());
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
        entity.setDateTo(dateTo.getConvertedInput());
        entity.setDateFrom(dateFrom.getConvertedInput());
        entity.setActivities(Optional.ofNullable(activities.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        entity.setRegions(Optional.ofNullable(regions.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        entity.setTck(tck.getConvertedInput());
        entity.setTimeOfDay(timeOfDay.getConvertedInput());
        entity.setUnit(unit.getConvertedInput());
        entity.setCanVoiceCall(voiceCall.getConvertedInput());
        entity.setPier(pier.getConvertedInput());
        entity.setCaptain(captain.getConvertedInput());
        setConvertedInput(entity);
    }

    @Override
    protected void initBeforeRenderer() {
        id.setModelObject(getModelObject());
        unit.setModelObject(getModel().map(Notification::getUnit).orElseGet(() -> unit.getModelObject()).getObject());
        year.setModelObject(getModel().map(Notification::getYear).getObject());
        number.setModelObject(getModel().map(Notification::getNumber).getObject());
        date.setModelObject(getModel().map(Notification::getDate).getObject());
        captain.setModelObject(getModel().map(Notification::getCaptain).getObject());
        boat.setModelObject(getModel().map(Notification::getBoat).getObject());
        dateTo.setModelObject(getModel().map(Notification::getDateTo).getObject());
        dateFrom.setModelObject(getModel().map(Notification::getDateFrom).getObject());
        activities.setModelObject(getModel().map(Notification::getActivities).map(Sets::newHashSet).getObject());
        regions.setModelObject(getModel().map(Notification::getRegions).map(Sets::newHashSet).getObject());
        tck.setModelObject(getModel().map(Notification::getTck).getObject());
        voiceCall.setModelObject(getModel().map(Notification::getCanVoiceCall).getObject());
        timeOfDay.setModelObject(getModel().map(Notification::getTimeOfDay).getObject());
        pier.setModelObject(getModel().map(Notification::getPier).getObject());
        //captain.setModelObject(getModel().map(Notification::getCaptain).getObject());


    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        captain.setVisible(!getModel().map(AbstractEntity::isNew).orElse(true).getObject());
    }

    public static class SplitByComaValidator extends AjaxFormComponentUpdatingBehavior implements IValidator<Collection<String>> {

        public SplitByComaValidator() {
            super("change");
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            System.out.println(target);
        }

        @Override
        public void validate(IValidatable<Collection<String>> validatable) {
            System.out.println(validatable.getValue());
        }
      /*  public CharSequence createAjaxLink(String entity) {
            CharSequence htmlLink = String.format("<a href=\"#\"><span class=\"fa fa-pencil\" onclick=\"%s\"></span></a>", createCallbackAjaxFunction(entity));
            //htmlLink = Strings.escapeMarkup(htmlLink);
            return htmlLink;
        }

        public CharSequence createCallbackAjaxFunction(E entity) {
            CharSequence url = getCallbackUrl(entity);
            CharSequence func = org.apache.wicket.util.string.Strings.escapeMarkup(String.format("Wicket.Ajax.get({'u':'%s'})", url));
            return func;
        }*/

    }
}
