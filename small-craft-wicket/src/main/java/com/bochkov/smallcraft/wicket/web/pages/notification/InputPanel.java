package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Notification;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class InputPanel extends GenericPanel<Notification> {


    public InputPanel(String id) {
        super(id);
    }

    public InputPanel(String id, IModel<Notification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FormComponentInputPanel("inputs", getModel()));
    }
}


