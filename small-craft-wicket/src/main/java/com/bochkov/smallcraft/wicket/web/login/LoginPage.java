package com.bochkov.smallcraft.wicket.web.login;

import com.bochkov.smallcraft.wicket.web.BasePage;
import com.bochkov.smallcraft.wicket.web.HomePage;
import com.giffing.wicket.spring.boot.context.scan.WicketSignInPage;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default login page.
 *
 * @author Marc Giffing
 */
@WicketSignInPage
@MountPath("login")
@RequireHttps
public class LoginPage extends BasePage<Void> {

    @Inject
    RememberMeServices rememberMeServices;

    public LoginPage(PageParameters parameters) {
        super(parameters);

        if (((AbstractAuthenticatedWebSession) getSession()).isSignedIn()) {
            continueToOriginalDestination();
        }
        add(new LoginForm("loginForm"));
    }

    private class LoginForm extends StatelessForm<LoginForm> {

        private String username;

        private String password;

        private boolean rememberme = true;

        public LoginForm(String id) {
            super(id);
            setModel(new CompoundPropertyModel<>(this));
            add(new FeedbackPanel("feedback"));
            add(new RequiredTextField<String>("username"));
            add(new PasswordTextField("password"));
            // add(new CheckBox("rememberme").setMarkupId("remember-me"));
        }

        @Override
        protected void onSubmit() {
            AuthenticatedWebSession session = AuthenticatedWebSession.get();
            if (session.signIn(username, password)) {
                setResponsePage(HomePage.class);
                HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
                HttpServletResponse response = (HttpServletResponse) getResponse().getContainerResponse();
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                rememberMeServices.loginSuccess(request, response, authentication);
            } else {
                error("Login failed");
            }
        }
    }
}
