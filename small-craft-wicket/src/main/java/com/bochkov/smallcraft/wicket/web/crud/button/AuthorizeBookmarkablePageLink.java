package com.bochkov.smallcraft.wicket.web.crud.button;

import com.bochkov.smallcraft.wicket.web.crud.DisabledAttributeBehavior;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeAction(action = Action.ENABLE, roles = "ROLE_ADMIN")
public class AuthorizeBookmarkablePageLink<T> extends BookmarkablePageLink<T> {

    public <C extends Page> AuthorizeBookmarkablePageLink(String id, Class<C> pageClass) {
        super(id, pageClass);
    }

    public <C extends Page> AuthorizeBookmarkablePageLink(String id, Class<C> pageClass, PageParameters parameters) {
        super(id, pageClass, parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new DisabledAttributeBehavior());
    }
}
