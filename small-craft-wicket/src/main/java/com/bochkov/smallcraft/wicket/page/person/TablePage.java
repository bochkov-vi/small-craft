package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;

@MountPath("person")
public class TablePage extends CrudTablePage<Person, Long> {

    @Inject
    PersonRepository repository;

    public TablePage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public PersonRepository getJpaRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("firstName"), "firstName", "firstName"));
        columns.add(new PropertyColumn(new ResourceModel("middleName"), "middleName", "middleName"));
        columns.add(new PropertyColumn(new ResourceModel("lastName"), "lastName", "lastName"));
        columns.add(new PropertyColumn(new ResourceModel("passport.serial"), "passport.serial", "passport.serial"));
        columns.add(new PropertyColumn(new ResourceModel("passport.number"), "passport.number", "passport.number"));
        columns.add(new PropertyColumn(new ResourceModel("passport.date"), "passport.date", "passport.date"));
        columns.add(new PropertyColumn(new ResourceModel("passport.data"), "passport.data", "passport.data"));
        columns.add(new PropertyColumn(new ResourceModel("phone"), "phone", "phone"));
        columns.add(new PropertyColumn(new ResourceModel("email"), "email", "email"));
        columns.add(new PropertyColumn(new ResourceModel("address"), "address", "address"));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public EditPage createEditPage() {
        return new EditPage();
    }
}
