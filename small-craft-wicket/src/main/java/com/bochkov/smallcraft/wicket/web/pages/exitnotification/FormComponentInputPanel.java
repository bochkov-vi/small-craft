package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.bootstrap.tempusdominus.localdatetime.LocalDateTimeTextFieldCalendar;
import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudPage;
import com.bochkov.smallcraft.wicket.web.pages.boat.SelectPier;
import com.bochkov.smallcraft.wicket.web.pages.notification.EditPage;
import com.bochkov.smallcraft.wicket.web.pages.notification.*;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.experimental.Accessors;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.springframework.data.jpa.domain.Specification;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    FormComponent<Boat> boat = new com.bochkov.smallcraft.wicket.web.pages.boat.FormComponentInputPanel("boat", PersistableModel.of(id -> boatRepository.findById(id))).setCanSelect(true);

    FormComponent<Person> captain = new CaptainPanel("captain", PersistableModel.of(id -> personRepository.findById(id)), boat.getModel().map(Boat::getPerson));

    FormComponent<Notification> notification = new SelectNotification("notification", PersistableModel.of(id -> notificationRepository.findById(id))) {

        public void onUpdate(AjaxRequestTarget target) {
            Optional<Notification> optional = Optional.ofNullable(getModelObject());
            if (optional.isPresent()) {
                if (Strings.isNullOrEmpty(pier.getModelObject())) {
                    optional.map(Notification::getPier).ifPresent(p -> pier.setModelObject(p));
                    target.add(pier);
                }
                if (!regions.getModel().isPresent().getObject() || regions.getModel().map(Collection::isEmpty).getObject() || optional.map(Notification::getBoat).isPresent()) {
                    Optional<Notification> n = notificationRepository.findTopByBoatOrderByNumberDesc(optional.map(Notification::getBoat).get());
                    n.map(Notification::getRegions).map(Sets::newHashSet).ifPresent(rg -> regions.setModelObject(rg));
                    target.add(regions);
                }
            }
        }
    }.setRequired(true);

    public FormComponentInputPanel(String id) {
        super(id);
    }

    public FormComponentInputPanel(String id, IModel<ExitNotification> model) {
        super(id, model);
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
        e.setNotification(notification.getConvertedInput());
        e.setUnit(unit.getConvertedInput());
        e.setPier(pier.getConvertedInput());
        e.setRegions(Optional.ofNullable(regions.getConvertedInput()).map(Sets::newHashSet).orElse(null));
        e.setReturnCallDateTime(returnCallDateTime.getConvertedInput());
        e.setReturnDateTime(returnDateTime.getConvertedInput());
        e.setActivities(activities.getConvertedInput() != null ? Sets.newHashSet(activities.getConvertedInput()) : null);
        e.setCaptain(captain.getConvertedInput());
        e.setEstimatedReturnDateTime(estimatedReturnDateTime.getConvertedInput());
        setConvertedInput(e);
    }

    @Override
    protected void initBeforeRenderer() {
        id.setModelObject(getModelObject());
        boat.setModelObject(getModel().map(ExitNotification::getBoat).orElse(null).getObject());
        Unit u = getModel().map(ExitNotification::getUnit).orElseGet(() -> unit.getModelObject()).getObject();
        unit.setModelObject(u);
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

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);
        content.add(new Label("number-code", id.getModel().map(ExitNotification::getId).map(BaseConverter::convert)) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isNullOrEmpty(this.getDefaultModelObjectAsString()));
            }
        });
        pier.setOutputMarkupId(true);
        regions.setOutputMarkupId(true);
        setOutputMarkupId(true);
        Optional<ExitNotification> entity = Optional.ofNullable(getModelObject());
        queue(id, estimatedReturnDateTime, exitCallDateTime, exitDateTime, returnCallDateTime, returnDateTime, pier, regions, boat, unit, activities, notification);
        notification.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getModelObject().putData(notification.getModelObject());
                target.add(pier, regions, activities, notification, captain, boat);
                initBeforeRenderer();
            }
        });


        queue(captain.setOutputMarkupId(true));
        Component editNotification = new AjaxLink<Notification>("edit-notification", LambdaModel.of(getModel(), ExitNotification::getNotification, ExitNotification::setNotification)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Page backPg = getPage();

                CrudEditPage notificationPage = new EditPage(getModel());
                notificationPage.setBackPage(backPg);
                setResponsePage(notificationPage);
            }
        };
        queue(editNotification);
        add(new IValidator<ExitNotification>() {
            @Override
            public void validate(IValidatable<ExitNotification> validatable) {
                if (validatable != null && validatable.getValue() != null) {
                    ExitNotification exit = validatable.getValue();
                    if (exit.getReturnDateTime() != null && exit.getExitDateTime() != null && !exit.getReturnDateTime().isAfter(exit.getExitDateTime())) {
                        exitCallDateTime.error(String.format("Время возвращения должно быть позже времени выхода"));
                    }

                    if (exit.getEstimatedReturnDateTime() != null && exit.getExitDateTime() != null && !exit.getEstimatedReturnDateTime().isAfter(exit.getExitDateTime())) {
                        exitCallDateTime.error(String.format("Ожидаемое время возвращения должно быть позже времени выхода"));
                    }
                    if (exit.getEstimatedReturnDateTime() != null && exit.getExitCallDateTime() != null && !exit.getEstimatedReturnDateTime().isAfter(exit.getExitCallDateTime())) {
                        exitCallDateTime.error(String.format("Ожидаемое время возвращения должно быть позже времени звонка на выход"));
                    }
                    if (exit.getReturnDateTime() != null && exit.getExitCallDateTime() != null && !exit.getReturnDateTime().isAfter(exit.getExitCallDateTime())) {
                        exitCallDateTime.error(String.format("Время возвращения должно быть позже времени звонка на выход"));
                    }
                    if (exit.getReturnCallDateTime() != null && exit.getExitCallDateTime() != null && !exit.getReturnCallDateTime().isAfter(exit.getExitCallDateTime())) {
                        exitCallDateTime.error(String.format("Время звонка о возвращения должно быть позже времени звонка на выход"));
                    }
                }
            }
        });

        add(new IValidator<ExitNotification>() {
            @Override
            public void validate(IValidatable<ExitNotification> validatable) {
                ExitNotification exitNotification = validatable.getValue();
                if (exitNotification != null) {
                    Notification notification = exitNotification.getNotification();
                    if (notification != null) {
                        LocalDateTime d1 = exitNotification.getExitDateTime();
                        if (d1 != null) {
                            LocalDateTime d2 = exitNotification.getReturnDateTime();
                            Specification<ExitNotification> specification = (r, q, b) -> {
                                Predicate predicate = b.and(b.equal(r.get("notification"), notification),
                                        b.lessThan(r.get("exitDateTime"), d2 != null ? d2 : LocalDateTime.now()),
                                        b.greaterThan(b.coalesce(r.get("returnDateTime"), LocalDateTime.now()), d1));
                                return predicate;
                            };
                            List<ExitNotification> duplicates = exitNotificationRepository.findAll(specification).stream().filter(exit -> !Objects.equals(exit, exitNotification)).collect(Collectors.toList());
                            if (!duplicates.isEmpty()) {
                                for (ExitNotification exit1 : duplicates) {
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
                                    error(String.format("Найдено пересечение периодов выхода: <a href='%1$s'>%2$s<a>", url, exit1.toString()));
                                }
                            }
                        }
                    }
                }
            }
        });
        /*notification.add(new DuplicateEntityBehavior<Notification, ExitNotification>(id.getModel(), ExitNotification.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, ExitNotification entity) {
                FormComponentInputPanel.this.setModelObject(entity);
                target.add(content);
            }

            @Override
            public List<ExitNotification> findDuplicates(Notification search) {
                Specification<ExitNotification> specification = (r, q, b) -> b.equal(r.get("notification"), search);
                ExitNotification exitNotification = getEntityModel().getObject();
                specification = specification.and((r, q, b) -> b.lessThanOrEqualTo(r.get("exitDateTime"), exitNotification.getReturnDateTime()));

                specification = specification.and((r, q, b) -> {
                    Predicate predicate = r.get("returnDateTime").isNull();
                    LocalDateTime exitDateTime = getEntityModel().map(ExitNotification::getExitDateTime).getObject();
                    LocalDateTime returnDateTime = getEntityModel().map(ExitNotification::getExitDateTime).getObject();
                    if (exitDateTime != null) {

                    }
                    return predicate;
                });
                return exitNotificationRepository.findAll(specification);
            }
        });*/
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
