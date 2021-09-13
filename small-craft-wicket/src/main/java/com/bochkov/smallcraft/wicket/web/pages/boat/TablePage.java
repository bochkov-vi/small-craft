package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.hierarchical.Hierarchicals;
import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.filter.FilterPanel;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeLink;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.base.Joiner;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import javax.persistence.criteria.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@MountPath("boat")
public class TablePage extends CrudTablePage<Boat, Long> {

    @Inject
    BoatRepository repository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    @Inject
    UnitRepository unitRepository;

    Long unit;

    String quickSearch;

    Boolean includeUnitChilds = true;

    Expirated expire;

    Boolean notRegistable = false;

    LocalDate dateFrom;

    LocalDate dateTo;

    public TablePage(PageParameters parameters) {
        super(Boat.class, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        FilterPanel filter = new FilterPanel("filter", new CompoundPropertyModel<>(this));
        add(filter);
        FormComponent<Expirated> expiratedDropDownChoice = new DropDownChoice<>("expire", com.google.common.collect.Lists.newArrayList(Expirated.values()), new EnumChoiceRenderer<>(getPage())).setNullValid(true);
        filter.add(expiratedDropDownChoice);
        filter.add(new CheckBox("notRegistable").setOutputMarkupId(true));
        filter.add(new LocalDateTextField("dateFrom", getString("dateFormat")));
        filter.add(new LocalDateTextField("dateTo", getString("dateFormat")));
    }

    @Override
    public BoatRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn<Boat, String>> columns() {
        List<IColumn<Boat, String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new AbstractColumn<Boat, String>(null, "notRegistable") {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> cellItem, String componentId, IModel<Boat> rowModel) {
                cellItem.add(new Label(componentId, new ResourceModel("notRegistable")));
                if (rowModel.map(Boat::isNotRegistable).map(bol -> !bol).getObject()) {
                    cellItem.setVisible(false);
                }
                cellItem.add(new AttributeModifier("colspan",
                        rowModel.filter(Boat::isNotRegistable).map(b -> 3).orElse(1)
                ));

            }

            /*@Override
            public void populateItem(Item cellItem, String componentId, IModel<Boat> rowModel) {

            }*/
        });

        columns.add(new PropertyColumn<Boat, String>(new ResourceModel("registrationNumber"), "registrationNumber", "registrationNumber") {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> item, String componentId, IModel<Boat> rowModel) {
                super.populateItem(item, componentId, rowModel);
                if (rowModel.map(Boat::isNotRegistable).getObject()) {
                    item.setVisible(false);
                }
                item.add(new AttributeModifier("colspan",
                        rowModel.filter(Boat::isNotRegistable).map(b -> 1).orElse(2)
                ));
            }
        });
        columns.add(new PropertyColumn<Boat, String>(new ResourceModel("registrationDate"), "registrationDate", "registrationDate") {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> item, String componentId, IModel<Boat> rowModel) {
                super.populateItem(item, componentId, rowModel);
                if (rowModel.map(Boat::isNotRegistable).getObject()) {
                    item.setVisible(false);
                }

            }
        });
        columns.add(new PropertyColumn(new ResourceModel("pier"), "pier", "pier"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "tailNumber", "tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "model"));
        columns.add(new PropertyColumn(new ResourceModel("power"), "power", "power"));
        columns.add(new PropertyColumn(new ResourceModel("serialNumber"), "serialNumber", "serialNumber"));
        columns.add(new PropertyColumn(new ResourceModel("buildYear"), "buildYear", "buildYear"));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("person"), "person.lastName", row -> Optional.ofNullable(row).map(Boat::getPerson).map(Person::toString).orElse(null)));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("phone"), "person.phones", row -> Optional.ofNullable(row).map(Boat::getPerson).map(Person::getPhones).map(phones -> Joiner.on(", ").join(phones)).orElse(null)));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("legalPerson"), "legalPerson.name", row -> Optional.ofNullable(row).map(Boat::getLegalPerson).map(Object::toString).orElse(null)));
        columns.add(new LambdaColumn<Boat, String>(new ResourceModel("unit"), "unit.name", row -> Optional.ofNullable(row).map(Boat::getUnit).map(Object::toString).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("expirationDate"), "expirationDate", "expirationDate"));

        columns.add(new HeaderlessColumn<Boat, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> cellItem, String componentId, IModel<Boat> rowModel) {
                cellItem.add(createNotificationLink(componentId, rowModel));
            }
        });
        columns.add(new HeaderlessColumn<Boat, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> cellItem, String componentId, IModel<Boat> rowModel) {
                cellItem.add(createExitNotificationLink(componentId, rowModel));
            }
        });
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Boat, Long>> getEditPageClass() {
        return EditPage.class;
    }

    public Specification<Boat> specification() {
        Specification<Boat> specification = Specification.where(null);

        specification = specification.and(Optional.ofNullable(quickSearch).map(str -> MaskableProperty.<Boat>maskSpecification(str, "registrationNumber", "tailNumber", "person.lastName", "legalPerson.name", "model")).orElse(null));
        if (includeUnitChilds) {
            specification = specification.and(Optional.ofNullable(unit).flatMap(id -> unitRepository.findById(id)).map(unitEntity -> Hierarchicals.getAllChildIds(true, unitEntity)).filter(list -> !list.isEmpty()).map(list -> (Specification<Boat>) (r, q, b) -> r.get("unit").get("id").in(list)).orElse(null));
        } else {
            specification = specification.and(Optional.ofNullable(unit).map(id -> (Specification<Boat>) (r, q, b) -> b.equal(r.get("unit").get("id"), id)).orElse(null));
        }
        specification = specification.and(Optional.ofNullable(expire).map(exp ->
                (Specification<Boat>) (r, q, b) -> {
                    Path path = r.get("expirationDate");
                    switch (expire) {
                        case NOT_EXPIRATED: {
                            return path.isNull();
                        }
                        case EXPIRATED: {
                            return path.isNotNull();

                        }
                        default: {
                            return null;
                        }
                    }
                }).orElse(null));
        if (notRegistable != null && notRegistable) {
            specification = specification.and((r, q, b) -> b.isTrue(r.get("notRegistable")));
        } else {
            specification = specification.and(Optional.ofNullable(dateFrom).map(df -> (Specification<Boat>) (r, q, b) -> b.greaterThanOrEqualTo(r.get("registrationDate"), df)).orElse(null));
            specification = specification.and(Optional.ofNullable(dateTo).map(dt -> (Specification<Boat>) (r, q, b) -> b.lessThanOrEqualTo(r.get("registrationDate"), dt)).orElse(null));
        }
        return specification;
    }

    public Component createNotificationLink(String componentId, IModel<Boat> rowModel) {
        Fragment fragment = new Fragment(componentId, "not-link", getPage());
        PersistableModel<Notification, Long> notification = PersistableModel.of(
                notificationRepository.findTopByBoatOrderByNumberDesc(rowModel.getObject(), LocalDate.now()).map(Persistable::getId).orElse(null),
                id -> notificationRepository.findById(id));
        PageParameters parameters = new PageParameters();
        parameters.set("boat", getConverter(Boat.class).convertToString(rowModel.getObject(), Session.get().getLocale()));
        Link link = new AuthorizeLink<Notification>("link", notification) {
            @Override
            public void onClick() {
                com.bochkov.smallcraft.wicket.web.pages.notification.EditPage notificationPage = new com.bochkov.smallcraft.wicket.web.pages.notification.EditPage(notification.copyWithIfNullGet(() -> {
                    Notification e = new Notification();
                    e.setBoat(rowModel.getObject());
                    e.setYear(LocalDate.now(SmallCraftWebSession.get().getZoneId()).getYear());
                    e.setDate(LocalDate.now(SmallCraftWebSession.get().getZoneId()));
                    e.setDateFrom(LocalDate.now(SmallCraftWebSession.get().getZoneId()));
                    e.setDateTo(LocalDate.now(SmallCraftWebSession.get().getZoneId()).with(TemporalAdjusters.lastDayOfYear()));
                    return e;
                }));
                notificationPage.setBackPage(getPage());
                setResponsePage(notificationPage);
                setSelected(rowModel);
            }
        };

        link.add(new Label("label", notification.map(Notification::getNumber).map(Objects::toString)));
        link.add(new AttributeAppender("title", notification.map(n -> String.format(getString("valid-until"), n.getDateTo())).orElseGet(() -> getString("create-new"))));
        fragment.add(link);
        return fragment;
    }

    public Component createExitNotificationLink(String componentId, IModel<Boat> rowModel) {
        Fragment fragment = new Fragment(componentId, "exit-link", getPage());
        PersistableModel<Notification, Long> notification = PersistableModel.of(
                notificationRepository.findTopByBoatOrderByNumberDesc(rowModel.getObject(), LocalDate.now()).map(Persistable::getId).orElse(null),
                id -> notificationRepository.findById(id));
        PersistableModel<ExitNotification, Long> exitNotification = PersistableModel.of(
                exitNotificationRepository.findLastByBoatAndPeriod(rowModel.getObject(), LocalDate.now()).orElse(null),
                id -> exitNotificationRepository.findById(id));
        Link<Boat> link = new AuthorizeLink<Boat>("link", rowModel) {
            @Override
            public void onClick() {
                com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage(
                        exitNotification.copyWithIfNullGet(
                                () -> {
                                    return new ExitNotification()
                                            .setBoat(rowModel.getObject())
                                            .setUnit(rowModel.map(Boat::getUnit).getObject())
                                            .setNotification(notification.getObject())
                                            .setPier(notification.map(Notification::getPier).orElse(null).getObject())
                                            .setActivities(notification.map(Notification::getActivities).orElse(null).getObject())
                                            .setRegions(notification.map(Notification::getRegions).orElse(null).getObject())
                                            .setCaptain(rowModel.map(Boat::getPerson).getObject())
                                            .setExitCallDateTime(LocalDateTime.now(SmallCraftWebSession.get().getZoneId()))
                                            .setExitDateTime(LocalDateTime.now(SmallCraftWebSession.get().getZoneId()));
                                })
                );
                editPage.setBackPage(getPage());
                setResponsePage(editPage);
                setSelected(rowModel);
            }
        };
        link.add(new Label("label", exitNotification.map(en -> getString("on-exit"))));
        fragment.add(link);
        return fragment;
    }


    public enum Expirated {
        EXPIRATED, NOT_EXPIRATED
    }
}
