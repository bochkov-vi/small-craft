package com.bochkov.smallcraft.wicket.web.pages.unit;

import org.apache.wicket.model.IModel;

public class SessionSelectUnitById extends SelectUnitById implements IIdUnitSelect {

    public SessionSelectUnitById(String id) {
        super(id);
    }

    public SessionSelectUnitById(String id, IModel<Long> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SessionUnitBehavior<>());
    }

    @Override
    public Long getIdUnit() {
        return getModelObject();
    }

    @Override
    public void setIdUnit(Long id) {
        setModelObject(id);
    }
}
