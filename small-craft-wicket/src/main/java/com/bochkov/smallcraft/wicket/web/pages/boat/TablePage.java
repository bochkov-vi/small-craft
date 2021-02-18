package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.base.Joiner;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.jpa.domain.Specification;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@MountPath("boat")
public class TablePage extends CrudTablePage<Boat, Long> {

    @Inject
    BoatRepository repository;

    @Inject
    NotificationRepository notificationRepository;

    BoatFilterPanel filterPanel = new BoatFilterPanel("filter");

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    public TablePage(PageParameters parameters) {
        super(Boat.class, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(filterPanel);

    }

    @Override
    public BoatRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn<Boat,String>> columns() {
        List<IColumn<Boat,String>> columns = Lists.newArrayList();
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
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "tailNumber", "tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "model"));
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
                Fragment fragment = new Fragment(componentId, "not-link", getPage());
                Link<Boat> link = new Link<Boat>("link", rowModel) {
                    @Override
                    public void onClick() {
                        PageParameters parameters = new PageParameters();
                        parameters.set("boat", getConverter(Boat.class).convertToString(getModelObject(), Session.get().getLocale()));
                        com.bochkov.smallcraft.wicket.web.pages.notification.EditPage notificationPage = new com.bochkov.smallcraft.wicket.web.pages.notification.EditPage(parameters);
                        notificationPage.setBackPage(getPage());
                        setResponsePage(notificationPage);
                    }
                };
                cellItem.add(fragment);
                fragment.add(link);
            }
        });
        columns.add(new HeaderlessColumn<Boat, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<Boat>> cellItem, String componentId, IModel<Boat> rowModel) {
                Fragment fragment = new Fragment(componentId, "exit-link", getPage());
                Link<Boat> link = new Link<Boat>("link", rowModel) {
                    @Override
                    public void onClick() {

                        com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage(
                                PersistableModel.of(id -> exitNotificationRepository.findById(id),
                                        () -> {
                                            Optional<Notification> n = notificationRepository.findTopByBoatOrderByNumberDesc(rowModel.getObject());
                                            return new ExitNotification()
                                                    .setBoat(rowModel.getObject())
                                                    .setUnit(rowModel.map(Boat::getUnit).getObject())
                                                    .setPier(n.map(Notification::getPier).orElse(null))
                                                    .setActivities(n.map(Notification::getActivities).orElse(null))
                                                    .setRegions(n.map(Notification::getRegions).orElse(null))
                                                    .setCaptain(rowModel.map(Boat::getPerson).getObject())
                                                    .setExitCallDateTime(LocalDateTime.now())
                                                    .setExitDateTime(LocalDateTime.now().plusHours(2));
                                        })
                        );
                        editPage.setBackPage(getPage());
                        setResponsePage(editPage);
                    }
                };
                cellItem.add(fragment);
                fragment.add(link);
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

    @Override
    protected Specification<Boat> specification() {
        return Specification.where(super.specification()).and(filterPanel.specification());
    }


}
