package com.bochkov.smallcraft.wicket.web.pages.account;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;

@MountPath("account/edit")
public class EditPage extends CrudEditPage<Account, String> {

    @Inject
    AccountRepository repository;


    public EditPage(PageParameters parameters) {
        super(Account.class, parameters);
    }

    public EditPage(IModel<Account> model) {
        super(Account.class, model);
    }

    public EditPage() {
        super(Account.class);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Account> model) {
        return new InputFields(id, model);
    }

    @Override
    protected AccountRepository getRepository() {
        return repository;
    }
}
