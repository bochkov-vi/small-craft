package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;

public class InputPanel extends GenericPanel<Unit> {


    public InputPanel(String id, IModel<Unit> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FormComponentInput("inputs", getModel()).setCanSelect(false));
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }


}
