package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MountPath("notification")
public class TablePage extends CrudTablePage<Notification, Long> {

    @Inject
    NotificationRepository repository;

    public TablePage(PageParameters parameters) {
        super(Notification.class, parameters);
    }

    @Override
    public NotificationRepository getJpaRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("year"), "year", "year"));
        columns.add(new PropertyColumn(new ResourceModel("registrationNumber"), "boat.registrationNumber", "boat.registrationNumber"));
        columns.add(new PropertyColumn(new ResourceModel("registrationDate"), "boat.registrationDate", "boat.registrationDate"));
        columns.add(new PropertyColumn(new ResourceModel("number"), "number", "number"));
        columns.add(new PropertyColumn(new ResourceModel("date"), "date", "date"));

        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.fio"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "boat.type", "boat.type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "boat.model", "boat.model"));
        columns.add(new PropertyColumn(new ResourceModel("tailNumber"), "boat.tailNumber", "boat.tailNumber"));
        columns.add(new PropertyColumn(new ResourceModel("pier"), "boat.pier", "boat.pier"));
        columns.add(new PropertyColumn(new ResourceModel("dateFrom"), "dateFrom", "dateFrom"));
        columns.add(new PropertyColumn(new ResourceModel("dateTo"), "dateTo", "dateTo"));
        columns.add(new PropertyColumn(new ResourceModel("activity"), "activity", "activity"));
        columns.add(new LambdaColumn<Notification, String>(new ResourceModel("region"), "region", row -> Optional.ofNullable(row).map(Notification::getRegion).map(set -> set.stream().collect(Collectors.joining("; "))).orElse(null)));
        columns.add(new PropertyColumn(new ResourceModel("timeOfDay"), "timeOfDay", "timeOfDay"));
        columns.add(new PropertyColumn(new ResourceModel("tck"), "tck", "tck"));
        columns.add(new PropertyColumn(new ResourceModel("unit"), "unit.name", "unit.name"));

        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Notification, Long>> getEditPageClass() {
        return EditPage.class;
    }


}
