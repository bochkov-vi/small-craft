package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.NotificationPK;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;

@MountPath("notification")
public class TablePage extends CrudTablePage<Notification, NotificationPK> {

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
        columns.add(new PropertyColumn(new ResourceModel("year"), "id.year", "id.year"));
        columns.add(new PropertyColumn(new ResourceModel("number"), "id.number", "id.number"));


        columns.add(new PropertyColumn(new ResourceModel("region"), "region", "region"));
        columns.add(new PropertyColumn(new ResourceModel("captain"), "captain", "captain.lastName"));
        columns.add(new PropertyColumn(new ResourceModel("boat"), "boat", "boat"));
        columns.add(new PropertyColumn(new ResourceModel("date"), "date", "date"));
        columns.add(new PropertyColumn(new ResourceModel("dateFrom"), "dateFrom", "dateFrom"));
        columns.add(new PropertyColumn(new ResourceModel("dateTo"), "dateTo", "dateTo"));
        columns.add(new PropertyColumn(new ResourceModel("activity"), "activity", "activity"));
        columns.add(new PropertyColumn(new ResourceModel("timeOfDay"), "timeOfDay", "timeOfDay"));
        columns.add(new PropertyColumn(new ResourceModel("tck"), "tck", "tck"));

        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public EditPage createEditPage() {
        return new EditPage();
    }
}
