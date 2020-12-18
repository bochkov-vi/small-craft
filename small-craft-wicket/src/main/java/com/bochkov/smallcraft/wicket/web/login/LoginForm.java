package com.bochkov.smallcraft.wicket.web.login;

import com.bochkov.smallcraft.wicket.web.HomePage;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class LoginForm extends StatelessForm<Void> {

    FormComponent<String> login = new TextField<>("login", Model.of(), String.class);
    FormComponent<String> password = new TextField<>("password", Model.of(), String.class);
    FormComponent<Boolean> rememberme = new CheckBox("remember-me", Model.of());

    public LoginForm(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (((AbstractAuthenticatedWebSession) getSession()).isSignedIn()) {
            continueToOriginalDestination();
        }

        setModel(new CompoundPropertyModel(this));
        add(login, password, rememberme);
    }

    @Override
    protected void onSubmit() {
        AuthenticatedWebSession session = AuthenticatedWebSession.get();
        if (session.signIn(login.getModelObject(), password.getModelObject())) {
            setResponsePage(HomePage.class);
        } else {
            error("Login failed");
        }
    }
}
