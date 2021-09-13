package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class InputPanel extends GenericPanel<ExitNotification> {


    public InputPanel(String id) {
        super(id);
    }

    public InputPanel(String id, IModel<ExitNotification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FormComponentInputPanel("inputs", getModel()));
    }
}


