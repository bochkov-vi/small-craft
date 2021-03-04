package com.bochkov.smallcraft.wicket.web.crud.button;

import com.bochkov.smallcraft.wicket.web.crud.DisabledAttributeBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.IModel;

@AuthorizeAction(action = Action.ENABLE, roles = "ROLE_ADMIN")
public abstract class AuthorizeAjaxLink<T> extends AjaxLink<T> {

    public AuthorizeAjaxLink(String id) {
        super(id);
    }

    public AuthorizeAjaxLink(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new DisabledAttributeBehavior());
    }
}
