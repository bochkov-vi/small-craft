package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.hierarchical.Hierarchicals;
import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.filter.FilterPanel;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.crud.EntityDataTable;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeLink;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@MountPath("notification")
public class TablePage extends CrudTablePage<Notification, Long> {

    @Inject
    NotificationRepository repository;

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    @Inject
    UnitRepository unitRepository;

    Long unit;

    String quickSearch;

    Boolean active = true;

    Boolean includeUnitChilds = true;

    LocalDate dateFrom;

    LocalDate dateTo;

    public TablePage(PageParameters parameters) {
        super(Notification.class, parameters);
    }

    @Override
    public NotificationRepository getRepository() {
        return repository;
    }

    public Specification specification() {
        return Specification.where(MaskableProperty.<Notification>maskSpecification(quickSearch, Lists.newArrayList("number", "captain.lastName", "boat.person.lastName", "boat.tailNumber", "boat.registrationNumber", "captain.phones", "boat.model")))
                .and(Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).map(unitEntity -> Hierarchicals.getAllChildIds(true, unitEntity)).filter(list -> !list.isEmpty()).map(list -> (Specification<Notification>) (r, q, b) -> r.get("unit").get("id").in(list)).orElse(null))
                .and(Optional.ofNullable(active).filter(b -> b).map(aBoolean -> (Specification<Notification>) (r, q, b) -> {
                    return b.greaterThanOrEqualTo(r.get("dateTo"), LocalDate.now());
                }).orElse(null))
                .and(Optional.ofNullable(dateFrom).map(ld -> (Specification<Notification>) (r, q, b) -> b.greaterThanOrEqualTo(r.get("date"), ld)).orElse(null))
                .and(Optional.ofNullable(dateTo).map(ld -> (Specification<Notification>) (r, q, b) -> b.lessThanOrEqualTo(r.get("date"), ld)).orElse(null));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FilterPanel("filter", new CompoundPropertyModel<>(this)));
        queue(new CheckBox("active").setOutputMarkupId(true));
        queue(new LocalDateTextField("dateFrom", getString("dateFormat")));
        queue(new LocalDateTextField("dateTo", getString("dateFormat")));
    }

    @Override
    protected List<? extends IColumn<Notification, String>> columns() {
        List<IColumn<Notification, String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("year"), "year", "year") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new AbstractColumn<Notification, String>(null, "boat.notRegistable") {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> cellItem, String componentId, IModel<Notification> rowModel) {
                cellItem.add(new Label(componentId, new ResourceModel("boat.notRegistable")));
                if (rowModel.map(Notification::getBoat).map(Boat::isNotRegistable).map(bol -> !bol).getObject()) {
                    cellItem.setVisible(false);
                }
                cellItem.add(new AttributeModifier("colspan",
                        rowModel.map(Notification::getBoat).filter(Boat::isNotRegistable).map(b -> 3).orElse(0)
                ));
                cellItem.add(new AttributeModifier("title", rowModel.map(Notification::getBoat).map(Boat::getId)));

            }


        });
        columns.add(new PropertyColumn<Notification, String>(new ResourceModel("registrationNumber"), "boat.registrationNumber", "boat.registrationNumber") {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> item, String componentId, IModel<Notification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                if (rowModel.map(Notification::getBoat).map(Boat::isNotRegistable).getObject()) {
                    item.setVisible(false);
                }
                item.add(new AttributeModifier("colspan",
                        rowModel.map(Notification::getBoat).filter(Boat::isNotRegistable).map(b -> 1).orElse(2)
                ));
            }
        });
        columns.add(new PropertyColumn<Notification, String>(new ResourceModel("registrationDate"), "boat.registrationDate", "boat.registrationDate") {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> item, String componentId, IModel<Notification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                if (rowModel.map(Notification::getBoat).map(Boat::isNotRegistable).getObject()) {
                    item.setVisible(false);
                }

            }
        });
        columns.add(new PropertyColumn(new ResourceModel("number"), "number", "number"));
        columns.add(new PropertyColumn(new ResourceModel("date"), "date", "date"));

        columns.add(new PropertyColumn<Notification, String>(new ResourceModel("captain"), "captain", "captain.fio") {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> item, String componentId, IModel<Notification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeModifier("title", rowModel.map(Notification::getCaptain).map(capt -> Joiner.on(' ').join(capt.getLastName(), capt.getFirstName(), capt.getMiddleName()))));
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("type"), "boat.type", "boat.type") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("model"), "boat.model", "boat.model"));
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("person"), "person", n -> Optional.ofNullable(n).map(Notification::getBoat)
                .map(Boat::getPersonAsString)
                .orElse(null)) {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> item, String componentId, IModel<Notification> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new AttributeModifier("title", rowModel.map(Notification::getBoat).map(b -> {
                    String fio = Optional.ofNullable(b.getPerson()).map(p -> Joiner.on(' ').join(p.getLastName(), p.getFirstName(), p.getMiddleName())).orElse(null);
                    String result = Optional.ofNullable(b.getLegalPerson()).map(lp -> String.format("%s (%s)", lp.toString(), fio)).orElse(fio);
                    return result;
                })));
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "boat.tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier") {
            @Override
            public String getCssClass() {
                return "d-none d-xl-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("dateFrom"), "dateFrom", "dateFrom"));
        columns.add(new PropertyColumn(new ResourceModel("dateTo"), "dateTo", "dateTo"));
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("activities"), "activities", e -> Optional.ofNullable(e.getActivities()).map(c -> c.stream().map(Objects::toString).collect(Collectors.joining("; "))).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-xl-table-cell";
            }

        });
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("regions"), "regions", row -> Optional.ofNullable(row).map(Notification::getRegions).map(set -> set.stream().distinct().collect(Collectors.joining("; "))).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-xl-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("timeOfDay"), "timeOfDay", "timeOfDay") {
            @Override
            public String getCssClass() {
                return "d-none d-xl-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("tck"), "tck", "tck") {
            @Override
            public String getCssClass() {
                return "d-none d-xl-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("unit"), "unit.name", "unit.name") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });


        columns.add(new HeaderlessColumn<Notification, String>() {

            @Override
            public String getCssClass() {
                return "";
            }

            @Override
            public void populateItem(Item<ICellPopulator<Notification>> cellItem, String componentId, IModel<Notification> rowModel) {
                Fragment fragment = new Fragment(componentId, "exit-link", getPage()) {
                    @Override
                    protected void onConfigure() {
                        setVisible(rowModel.map(n -> n.isExpired()).map(b -> !b).getObject());
                        super.onConfigure();
                    }
                };

                AbstractLink link = new AuthorizeLink<Notification>("link", rowModel) {
                    @Override
                    public void onClick() {
                        com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage(PersistableModel.of(
                                id -> exitNotificationRepository.findById(id),
                                () -> getModel().map(n -> ExitNotification.of(n)).getObject()
                        ));
                        editPage.setBackPage(getPage());
                        setResponsePage(editPage);
                    }
                };
                link.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        oldClasses.addAll(com.google.common.collect.Lists.newArrayList("btn", "btn-outline-success"));
                        return oldClasses;
                    }
                });
                fragment.add(link);
                cellItem.add(fragment);
                fragment.add(new Label("expired", new ResourceModel("expired")) {
                    @Override
                    protected void onConfigure() {
                        setVisible(rowModel.map(n -> n.isExpired()).getObject());
                        super.onConfigure();
                    }
                });
            }
        });
        columns.add(new PropertyColumn<Notification, String>(new ResourceModel("canVoiceCall"), "canVoiceCall", "canVoiceCall") {
            @Override
            public void populateItem(Item<ICellPopulator<Notification>> item, String componentId, IModel<Notification> rowModel) {
                item.add(new AjaxLink<Notification>(componentId, rowModel) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        Notification n = getModelObject();
                        n.setCanVoiceCall(!n.getCanVoiceCall());
                        getRepository().safeSave(n);
                        target.add(item);
                        target.appendJavaScript("$('.tooltip').remove()");
                        target.appendJavaScript("$('[title]').tooltip()");
                    }
                }.setBody(rowModel.map(Notification::getCanVoiceCall).map(b -> b ? "<div class='btn btn-outline-secondary' title='Голосовые уведомления включены'><i class='fa fa-microphone' ></i></div>" : "<div class='btn btn-outline-warning' title='Голосовые уведомления не доступны'><i class='fa fa-microphone-slash '></i></div>")).setEscapeModelStrings(false));
                item.setOutputMarkupId(true);
            }
        });
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Notification, Long>> getEditPageClass() {
        return EditPage.class;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(TablePage.class, "TablePage.css")));
    }

    @Override
    public void onRowCreated(EntityDataTable<Notification, Long> table, Item<Notification> row, String id, int index, IModel<Notification> model) {
        row.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (model.map(n -> n.isExpiredDate(LocalDateTime.now())).orElse(false).getObject()) {
                    // oldClasses.add("expired-row");
                }
                return oldClasses;
            }
        });
    }

    /* @Override
    protected Item<Notification> onRowCreated(EntityDataTable<Notification, Long> table, String id, int index, IModel<Notification> model) {

        row.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (model.map(n -> !n.isValidExit(LocalDateTime.now())).orElse(false).getObject()) {
                    oldClasses.add("expired-row");
                }
                return oldClasses;
            }
        });
        return row;
    }*/
}
