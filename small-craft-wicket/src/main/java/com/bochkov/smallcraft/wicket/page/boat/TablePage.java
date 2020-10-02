package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;

@MountPath("boat")
public class TablePage extends CrudTablePage<Boat, String> {

    @Inject
    BoatRepository repository;

    public TablePage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public BoatRepository getJpaRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new PropertyColumn(new ResourceModel("type"), "type", "type"));
        columns.add(new PropertyColumn(new ResourceModel("model"), "model", "model"));
        columns.add(new PropertyColumn(new ResourceModel("own"), "own", "own"));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public EditPage createEditPage() {
        return new EditPage();
    }
}
