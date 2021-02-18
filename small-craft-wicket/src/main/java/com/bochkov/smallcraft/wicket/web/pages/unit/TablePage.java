package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.google.common.base.Joiner;
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

@MountPath("unit")
public class TablePage extends CrudTablePage<Unit, Long> {

    @Inject
    UnitRepository repository;

    public TablePage(PageParameters parameters) {
        super(Unit.class, parameters);
    }

    @Override
    public UnitRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn<Unit,String>> columns() {
        List<IColumn<Unit,String>> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new PropertyColumn(new ResourceModel("name"), "name", "name"));
        columns.add(new PropertyColumn(new ResourceModel("phone"), "phone", "phone"));
        columns.add(new LambdaColumn<Unit, String>(new ResourceModel("parents"), unit -> Optional.ofNullable(unit).map(Unit::getParents).map(list -> Joiner.on("; ").join(list)).orElse(null)));
        columns.add(new LambdaColumn<Unit, String>(new ResourceModel("childs"), unit -> Optional.ofNullable(unit).map(Unit::getChilds).map(list -> Joiner.on("; ").join(list)).orElse(null)));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<EditPage> getEditPageClass() {
        return EditPage.class;
    }
}
