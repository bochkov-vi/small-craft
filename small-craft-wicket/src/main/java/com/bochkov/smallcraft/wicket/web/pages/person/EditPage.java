package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.crud.EntityDataTable;
import com.bochkov.wicket.data.provider.PersistableDataProvider;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Joiner;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@MountPath("person/edit")
public class EditPage extends CrudEditPage<Person, Long> {

    @SpringBean
    PersonRepository repository;

    @SpringBean
    LegalPersonRepository legalPersonRepository;

    @SpringBean
    NotificationRepository notificationRepository;

    @SpringBean
    BoatRepository boatRepository;

    @SpringBean
    ExitNotificationRepository exitNotificationRepository;

    public EditPage(PageParameters parameters) {
        super(Person.class, parameters);
    }

    public EditPage(IModel<Person> model) {
        super(Person.class, model);
    }

    public EditPage() {
        super(Person.class);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        feedback.setEscapeModelStrings(false);
        add(new ListView<Boat>("boats", LoadableDetachableModel.of(() -> getModel().filter(p -> !p.isNew()).map(p -> boatRepository.findBoatsByPerson(p)).getObject())) {
            @Override
            protected void populateItem(ListItem<Boat> item) {
                item.add(new Label("boat", item.getModel().map(Boat::toString)));
                item.add(CrudTablePage.createEditButton("edit", PersistableModel.of(item.getModelObject(), btpk -> boatRepository.findById(btpk)),
                        boatModel -> {
                            com.bochkov.smallcraft.wicket.web.pages.boat.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.boat.EditPage();
                            editPage.setModel(boatModel);
                            editPage.setBackPage(getPage());
                            setResponsePage(editPage);
                        },
                        getPage()));
            }
        });
        add(new ListView<Notification>("notifications", LoadableDetachableModel.of(() -> getModel().filter(p -> !p.isNew()).map(p -> notificationRepository.findByCaptainOrBoatPerson(p, LocalDate.now(SmallCraftWebSession.get().getZoneId()))).getObject())) {
            @Override
            protected void populateItem(ListItem<Notification> item) {
                item.add(new Label("boat", item.getModel().map(Notification::getBoat).map(Boat::toString)));
                item.add(new Label("notification", item.getModel().map(Notification::getNumber)));
                item.add(new Label("year", item.getModel().map(Notification::getYear)));
                item.add(new Label("dateFrom", item.getModel().map(Notification::getDateFrom).map(d -> d.format(DateTimeFormatter.ofPattern(getString("dateFormat"))))));
                item.add(new Label("dateTo", item.getModel().map(Notification::getDateTo).map(d -> d.format(DateTimeFormatter.ofPattern(getString("dateFormat"))))));
                item.add(CrudTablePage.createEditButton("edit", PersistableModel.of(item.getModelObject(), ntpk -> notificationRepository.findById(ntpk)),
                        model -> {
                            com.bochkov.smallcraft.wicket.web.pages.notification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.notification.EditPage(model);
                            setResponsePage(editPage);
                            editPage.setBackPage(this.getPage());
                        }, EditPage.this.getPage()
                ));
            }
        });
        List<IColumn<ExitNotification, String>> exitColumns = exitNotificationColumns();
        ISortableDataProvider<ExitNotification, String> provider = PersistableDataProvider.of(() -> exitNotificationRepository, () -> (Specification<ExitNotification>) (r, q, b) -> {
            return b.equal(r.get("captain"), EditPage.this.getModelObject());
        }, () -> Sort.by(Sort.Order.desc("exitDateTime")));

        EntityDataTable<ExitNotification, Long> exits = new EntityDataTable<ExitNotification, Long>("exits", exitColumns, provider, 50);
        add(exits);

    }


    List<IColumn<ExitNotification, String>> exitNotificationColumns() {
        List<IColumn<ExitNotification, String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.notification.number"), "notification.number", "notification.number"));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("id"), "id", en -> BaseConverter.convert(en.getId())) {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> item, String componentId, IModel<ExitNotification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeAppender("title", rowModel.map(ExitNotification::getId)));
            }
        });
        columns.add(new PropertyColumn<ExitNotification, String>(new ResourceModel("exitNotification.captain"), "captain", "captain.fio") {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> item, String componentId, IModel<ExitNotification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeAppender("title", rowModel.map(ExitNotification::getCaptain).map(Person::getFullFio)));
            }
        });
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("exitNotification.phones"), n -> Optional.ofNullable(n.getCaptain()).map(Person::getPhones).map(list -> Joiner.on("; ").join(list)).orElse(null)) {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                IModel<String> phones = rowModel.map(ExitNotification::getCaptain).map(Person::getPhones).map(list -> String.join("; ", list));
                Label label = new Label(componentId, phones.map(str -> String.format("%s...", StringUtils.substring(str, 0, 17))));
                cellItem.add(label);
                cellItem.add(new AttributeModifier("title", phones));
                cellItem.add(new AttributeModifier("data-toggle", "tooltip"));
                label.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {

                        oldClasses.add("d-block");
                        return oldClasses;
                    }
                });
                label.add(new StyleAttributeModifier() {
                    @Override
                    protected Map<String, String> update(Map<String, String> oldStyles) {
                        oldStyles.put("text-overflow", "ellipsis");
                        //oldStyles.put("display", "block");
                        oldStyles.put("white-space", "nowrap");
                        return oldStyles;
                    }
                });
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.type"), "type", "boat.type"));
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.model"), "model", "boat.model"));
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.tailNumber"), "tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.pier"), "pier", "pier"));
        columns.add(new LambdaColumn<>(new ResourceModel("exitNotification.regions"), "regions", row -> Optional.ofNullable(row).map(ExitNotification::getRegions).map(set -> set.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new LambdaColumn<>(new ResourceModel("exitNotification.exitDateTime"), "exitDateTime", en -> format(en.getExitDateTime())));
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("exitNotification.estimatedReturnDateTime"), "estimatedReturnDateTime", en -> format(en.getEstimatedReturnDateTime())) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new AbstractColumn<ExitNotification, String>(new ResourceModel("exitNotification.returnDateTime"), "returnDateTime") {
            @Override
            public void populateItem(Item<ICellPopulator<ExitNotification>> cellItem, String componentId, IModel<ExitNotification> rowModel) {
                cellItem.add(new Label(componentId, rowModel.map(ExitNotification::getReturnDateTime).map(rdt -> format(rdt))));
            }
        });
        columns.add(new LambdaColumn<ExitNotification, String>(new ResourceModel("exitNotification.unit"), "unit.name", row -> Optional.ofNullable(row).map(ExitNotification::getUnit).map(Unit::getName).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });

//        columns.add(new PropertyColumn(new ResourceModel("activity"), "activity", "activity"));
        columns.add(new PropertyColumn(new ResourceModel("exitNotification.creator"), "creator", "creator"));
        return columns;
    }

    @Override
    protected Component createInputPanel(String id, IModel<Person> model) {
        return new InputPanel(id, model) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedback);
            }
        };
    }

    @Override
    public PersonRepository getRepository() {
        return repository;
    }

    @Override
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onAfterSave(target, model);
    }

    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onSave(target, model);
    }


    @Override
    public Person newEntityInstance() {
        return new Person().setPassport(new Passport());
    }

}
