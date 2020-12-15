package com.bochkov.smallcraft.wicket.page;

import com.bochkov.bootstrap.ActiveLinkBehavior;
import com.bochkov.bootstrap.BootstrapBehavior;
import com.bochkov.fontawesome.FontAwesomeBehavior;
import com.bochkov.smallcraft.wicket.page.store.boat.TablePage;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import java.time.LocalDate;
import java.util.Optional;

public class BasePage<T> extends GenericWebPage<T> {

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
        add(new BookmarkablePageLink<Void>("person-link", com.bochkov.smallcraft.wicket.page.store.person.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("legal-person-link", com.bochkov.smallcraft.wicket.page.store.legalPerson.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("notification-link", com.bochkov.smallcraft.wicket.page.store.notification.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("exit-notification-link", com.bochkov.smallcraft.wicket.page.store.exitnotification.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
        add(new BookmarkablePageLink<Void>("unit-link", com.bochkov.smallcraft.wicket.page.store.unit.TablePage.class).add(ActiveLinkBehavior.forBookmarkable()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        String contextPath = WebApplication.get().getServletContext().getContextPath();
        response.render(MetaDataHeaderItem.forLinkTag("icon", contextPath + "/res/img/boat-icon.png"));
    }
}
