package com.bochkov.smallcraft.wicket.web;

import com.bochkov.bootstrap.ActiveLinkBehavior;
import com.bochkov.bootstrap.BootstrapBehavior;
import com.bochkov.fontawesome.FontAwesomeBehavior;
import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.login.LoginPage;
import com.bochkov.smallcraft.wicket.web.pages.boat.TablePage;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Optional;
import java.util.TimeZone;

public class BasePage<T> extends GenericWebPage<T> {

    @Inject
    RememberMeServices rememberMeServices;

    public BasePage() {
    }

    public BasePage(IModel<T> model) {
        super(model);
    }

    public BasePage(PageParameters parameters) {
        super(parameters);
    }

    public static Optional<LocalDate> date(StringValue value) {
        Optional<LocalDate> date = Optional.ofNullable(value)
                .map(StringValue::toOptionalString)
                .filter(str -> !Strings.isEmpty(str))
                .map(str -> {
                    try {
                        return Application.get().getConverterLocator().getConverter(LocalDate.class).convertToObject(str, Session.get().getLocale());
                    } catch (ConversionException e) {
                        return null;
                    }
                });
        return date;
    }

    public static String string(LocalDate value) {
        return Application.get().getConverterLocator().getConverter(LocalDate.class).convertToString(value, Session.get().getLocale());
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new BootstrapBehavior());
        add(new FontAwesomeBehavior());
        add(new BookmarkablePageLink<Void>("home-link", HomePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("boat-link", TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("person-link", com.bochkov.smallcraft.wicket.web.pages.person.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("legal-person-link", com.bochkov.smallcraft.wicket.web.pages.legalPerson.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("notification-link", com.bochkov.smallcraft.wicket.web.pages.notification.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("exit-notification-link", com.bochkov.smallcraft.wicket.web.pages.exitnotification.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("unit-link", com.bochkov.smallcraft.wicket.web.pages.unit.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        IModel<Account> accountIModel = LoadableDetachableModel.of(() -> {
            SmallCraftWebSession session = SmallCraftWebSession.get();
            Account account = session.getCurrentAccount();
            return account;
        });

        AjaxLink btnSignOut = new AjaxLink<Void>("btn-signout") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SmallCraftWebSession.get().signOut();
                getRequestCycle().setResponsePage(getPage());
                HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
                HttpServletResponse response = (HttpServletResponse) getResponse().getContainerResponse();
                rememberMeServices.loginFail(request, response);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(accountIModel.isPresent().getObject());
            }
        };
        btnSignOut.add(new Label("account-label", accountIModel.map(Account::getId)));
        add(btnSignOut);
        add(new AjaxLink<Void>("btn-signin") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getRequestCycle().setResponsePage(LoginPage.class);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!accountIModel.isPresent().getObject());
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        String contextPath = WebApplication.get().getServletContext().getContextPath();
        response.render(MetaDataHeaderItem.forLinkTag("icon", contextPath + "/res/img/fishing-boat-icon-3.png"));
    }
}
