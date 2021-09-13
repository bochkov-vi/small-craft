package com.bochkov.smallcraft.wicket.web.crud;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;

public abstract class CompositeInputPanel<T> extends FormComponentPanel<T> {

    public CompositeInputPanel(String id) {
        super(id);
    }

    public CompositeInputPanel(String id, IModel<T> model) {
        super(id, model);
    }

    public boolean formHasError() {
        return getForm().hasError();
    }

    abstract protected void initBeforeRenderer();

    @Override
    protected final void onBeforeRender() {
        if (!formHasError()) {
            initBeforeRenderer();
        }
        super.onBeforeRender();
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
