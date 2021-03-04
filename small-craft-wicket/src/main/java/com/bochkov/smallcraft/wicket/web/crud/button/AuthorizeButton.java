package com.bochkov.smallcraft.wicket.web.crud.button;

import com.bochkov.smallcraft.wicket.web.crud.DisabledAttributeBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;

@AuthorizeAction(action = Action.ENABLE, roles = "ROLE_ADMIN")
public class AuthorizeButton extends Button {

    public AuthorizeButton(String id) {
        super(id);
    }

    public AuthorizeButton(String id, IModel<String> model) {
        super(id, model);
    }
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new DisabledAttributeBehavior());
    }
}
