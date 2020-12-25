package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@MountPath("person")
public class TablePage extends CrudTablePage<Person, Long> {

    @Inject
    PersonRepository repository;

    public TablePage(PageParameters parameters) {
        super(Person.class, parameters);
    }

    @Override
    public PersonRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));

        columns.add(new LambdaColumn<Person, String>(new ResourceModel("lastName"), "lastName", Person::toString));
        columns.add(new PropertyColumn(new ResourceModel("passport.serial"), "passport.serial", "passport.serial"));
        columns.add(new PropertyColumn(new ResourceModel("passport.number"), "passport.number", "passport.number"));
        columns.add(new PropertyColumn(new ResourceModel("passport.date"), "passport.date", "passport.date"));
        columns.add(new PropertyColumn(new ResourceModel("passport.data"), "passport.data", "passport.data"));
        columns.add(new LambdaColumn<Person, String>(new ResourceModel("phones"), "phones", p -> p.getPhones().stream().collect(Collectors.joining("; "))));
        columns.add(new PropertyColumn(new ResourceModel("address"), "address", "address"));
        columns.add(new PropertyColumn(new ResourceModel("birthDate"), "birthDate", "birthDate"));
        columns.add(new PropertyColumn(new ResourceModel("email"), "email", "email"));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<? extends CrudEditPage<Person, Long>> getEditPageClass() {
        return EditPage.class;
    }


}
