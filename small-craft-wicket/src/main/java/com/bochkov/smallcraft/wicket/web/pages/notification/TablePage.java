package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.wicket.data.model.PersistableModel;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@MountPath("notification")
public class TablePage extends CrudTablePage<Notification, Long> {

    @Inject
    NotificationRepository repository;

    @Inject
    ExitNotificationRepository exitNotificationRepository;

    public TablePage(PageParameters parameters) {
        super(Notification.class, parameters);
    }

    @Override
    public NotificationRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("year"), "year", "year") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("registrationNumber"), "boat.registrationNumber", "boat.registrationNumber"));
        columns.add(new PropertyColumn(new ResourceModel("registrationDate"), "boat.registrationDate", "boat.registrationDate"));
        columns.add(new PropertyColumn(new ResourceModel("number"), "number", "number"));
        columns.add(new PropertyColumn(new ResourceModel("date"), "date", "date"));

        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.fio"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "boat.type", "boat.type") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("model"), "boat.model", "boat.model"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "boat.tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "boat.pier", "boat.pier") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("dateFrom"), "dateFrom", "dateFrom"));
        columns.add(new PropertyColumn(new ResourceModel("dateTo"), "dateTo", "dateTo"));
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("activities"), "activities", e -> Optional.ofNullable(e.getActivities()).map(c -> c.stream().map(Objects::toString).collect(Collectors.joining("; "))).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }

        });
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("region"), "region", row -> Optional.ofNullable(row).map(Notification::getRegion).map(set -> set.stream().distinct().collect(Collectors.joining("; "))).orElse(null)) {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("timeOfDay"), "timeOfDay", "timeOfDay") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
            }
        });
        columns.add(new PropertyColumn(new ResourceModel("tck"), "tck", "tck") {
            @Override
            public String getCssClass() {
                return "d-none d-lg-table-cell";
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
            public void populateItem(Item<ICellPopulator<Notification>> cellItem, String componentId, IModel<Notification> rowModel) {
                ExitNotification exitNotification = rowModel.map(Notification::getBoat).map(boat -> exitNotificationRepository.findByBoatAndPeriod(boat).orElse(null)).getObject();
                AjaxLink<ExitNotification> link = new AjaxLink<ExitNotification>(componentId, PersistableModel.of(exitNotification, id -> exitNotificationRepository.findById(id))) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage(
                                PersistableModel.of(id -> exitNotificationRepository.findById(id),
                                        ()->{
                                            ExitNotification exitNotification
                                        })
                        );

                    }
                };
                link.setBody(Model.of("<span class='fa fa-exchange'></span>")).setEscapeModelStrings(false);
                cellItem.add(link);
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


}
