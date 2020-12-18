package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class InputPanel extends GenericPanel<Boat> {


    public InputPanel(String id, IModel<Boat> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FormComponentInputPanel("inputs", getModel()));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    protected void onUpdate(AjaxRequestTarget target) {
    }


}
