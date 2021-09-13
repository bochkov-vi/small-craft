package com.bochkov.smallcraft.wicket.web.pages.account;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.google.common.collect.Lists;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AuthorizeInstantiation("ROLE_ADMIN")
@MountPath("account")
public class TablePage extends CrudTablePage<Account, String> {

    @Inject
    AccountRepository repository;

    public TablePage() {
        super(Account.class);
    }

    public TablePage(PageParameters parameters) {
        super(Account.class, parameters);
    }

    @Override
    protected AccountRepository getRepository() {
        return repository;
    }

    @Override
    public Class<EditPage> getEditPageClass() {
        return EditPage.class;
    }

    @Override
    protected List<? extends IColumn<Account, String>> columns() {
        return Lists.newArrayList(
                new PropertyColumn<>(new ResourceModel("id"), "id", "id"),
                new PropertyColumn<>(new ResourceModel("unit"), "unit.name", "unit.name"),
                new LambdaColumn<>(new ResourceModel("roles"), a -> a.getRoles().stream().map(Objects::toString).collect(Collectors.joining(", "))),
                createEditColumn(),
                createDeleteColumn()
        );
    }
}
