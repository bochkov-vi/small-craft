package com.bochkov.smallcraft.wicket.web;

import com.bochkov.smallcraft.wicket.web.pages.boat.TablePage;
import com.bochkov.smallcraft.wicket.web.pages.dashboard.DashBoardPanel;
import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;

@MountPath("home")
@WicketHomePage
public class HomePage extends BasePage<Void> {

    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
        super(parameters);
    }

    public HomePage() {
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new BookmarkablePageLink<Void>("home", HomePage.class));
        add(new BookmarkablePageLink<Void>("boat", TablePage.class));
        add(new BookmarkablePageLink<Void>("person", com.bochkov.smallcraft.wicket.web.pages.person.TablePage.class));
        add(new BookmarkablePageLink<Void>("legal", com.bochkov.smallcraft.wicket.web.pages.legalPerson.TablePage.class));
        add(new BookmarkablePageLink<Void>("notification", com.bochkov.smallcraft.wicket.web.pages.notification.TablePage.class));
        add(new BookmarkablePageLink<Void>("exit-notification", com.bochkov.smallcraft.wicket.web.pages.notification.TablePage.class));
        add(new BookmarkablePageLink<Void>("unit", com.bochkov.smallcraft.wicket.web.pages.unit.TablePage.class));
        add(new DashBoardPanel("dashboard", LoadableDetachableModel.of(LocalDate::now)));
    }
}
