package com.bochkov.smallcraft.wicket.web;

import com.bochkov.bootstrap.ActiveLinkBehavior;
import com.bochkov.bootstrap.BootstrapBehavior;
import com.bochkov.fontawesome.FontAwesomeBehavior;
import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeBookmarkablePageLink;
import com.bochkov.smallcraft.wicket.web.login.LoginPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.wicket.Application;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.reflections.Reflections;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Reflections reflections = new Reflections("com.bochkov.smallcraft");
        List<Class> classes = Lists.newArrayList(reflections.getSubTypesOf(CrudTablePage.class));
        classes.add(Application.get().getHomePage());
        Collections.sort(classes, Ordering.natural().nullsFirst().onResultOf(c -> loadString((Class) c, "page-order")));
        RepeatingView links = new RepeatingView("links");
        for (Class<? extends CrudTablePage> pageClass : classes) {
            IModel title = IModel.of(() -> loadString(pageClass, "title"));
            String iconName = loadString(pageClass, "icon");

            AuthorizeInstantiation authorizeInstantiation = pageClass.getAnnotation(AuthorizeInstantiation.class);
            BookmarkablePageLink link = null;
            if (authorizeInstantiation != null) {
                link = new AuthorizeBookmarkablePageLink(links.newChildId(), pageClass);
            } else {
                link = new BookmarkablePageLink<Void>(links.newChildId(), pageClass);
            }
            link.add(ActiveLinkBehavior.forBookmarkable());
            link.setEscapeModelStrings(false);
            Label label = new Label("label", title);
            link.add(label);
            Label icon = new Label("icon");
            if (!Strings.isEmpty(iconName)) {
                label.add(new ClassAttributeModifier() {
                    @Override
                    protected Set<String> update(Set<String> oldClasses) {
                        oldClasses.add("fa");
                        oldClasses.add(iconName);
                        return oldClasses;
                    }
                });
            } else {
                icon.setVisible(false);
            }
            link.add(icon);
            links.add(link);
        }
        add(links);

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

    public String loadString(Class clazz, String key) {
        for (IStringResourceLoader sl :
                Application.get().getResourceSettings().getStringResourceLoaders()) {
            String value = sl.loadStringResource(clazz, key, Session.get().getLocale(), Session.get().getStyle(), null);
            if (!Strings.isEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    public String format(LocalDate localDate) {
        return Optional.ofNullable(localDate).map(ld -> ld.format(DateTimeFormatter.ofPattern(getString("dateFormat")))).orElse(null);
    }
    public String format(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(ld -> ld.format(DateTimeFormatter.ofPattern(getString("dateTimeFormat")))).orElse(null);
    }

}
