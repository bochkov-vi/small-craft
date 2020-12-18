package com.bochkov.smallcraft.wicket.web.pages.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;

public class InputPanel extends GenericPanel<LegalPerson> {


    public InputPanel(String id, IModel<LegalPerson> model) {
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
