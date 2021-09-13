package com.bochkov.smallcraft.wicket.web.pages.account;

import com.bochkov.smallcraft.jpa.entity.AbstractEntity;
import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

public class InputFields extends CompositeInputPanel<Account> {


    @Inject
    UnitRepository unitRepository;

    @Inject
    AccountRepository accountRepository;

    FormComponent<String> id = new TextField<>("id", Model.of(),String.class).setRequired(true);

    FormComponent<Unit> unit = new SessionSelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<Collection<String>> roles = new CheckBoxMultipleChoice<String>("roles", new ListModel<String>(), Lists.newArrayList("ROLE_USER", "ROLE_ADMIN"));

    FormComponent<String> password = new PasswordTextField("password", Model.of());

    public InputFields(String id) {
        super(id);
    }

    public InputFields(String id, IModel<Account> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(id, password, roles, unit);
    }

    @Override
    protected void initBeforeRenderer() {
        id.setModelObject(getModel().map(Account::getId).orElse(null).getObject());
        password.setModelObject(getModel().map(Account::getPassword).getObject());
        roles.setModelObject(getModel().map(Account::getRoles).map(Lists::newArrayList).getObject());
        unit.setModelObject(getModel().map(Account::getUnit).getObject());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getModel().map(AbstractEntity::isNew).getObject()) {
            id.setEnabled(true);
        } else {
            id.setEnabled(false);
        }
    }

    @Override
    public void convertInput() {
        Account account = getModelObject();
        if (account == null || account.isNew()) {
            account = new Account().setId(id.getConvertedInput());
        }
        account.setPassword(password.getConvertedInput())
                .setRoles(Optional.ofNullable(roles.getConvertedInput()).map(Lists::newArrayList).orElse(null))
                .setUnit(unit.getConvertedInput());
        setConvertedInput(account);
    }


}
