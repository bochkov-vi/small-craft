package com.bochkov.smallcraft.wicket.page.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
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

@MountPath("person/legal")
public class TablePage extends CrudTablePage<LegalPerson, Long> {

    @Inject
    LegalPersonRepository repository;

    public TablePage(PageParameters parameters) {
        super(LegalPerson.class, parameters);
    }

    @Override
    public LegalPersonRepository getRepository() {
        return repository;
    }


    @Override
    protected List<? extends IColumn> columns() {
        List<IColumn> columns = Lists.newArrayList();
        columns.add(new PropertyColumn(new ResourceModel("id"), "id", "id"));
        columns.add(new PropertyColumn(new ResourceModel("name"), "name", "name"));
        columns.add(new PropertyColumn(new ResourceModel("address"), "address", "address"));
        columns.add(new PropertyColumn(new ResourceModel("inn"), "inn", "inn"));
        columns.add(createEditColumn());
        columns.add(createDeleteColumn());
        return columns;
    }

    @Override
    public Class<EditPage> getEditPageClass() {
        return EditPage.class;
    }
}
