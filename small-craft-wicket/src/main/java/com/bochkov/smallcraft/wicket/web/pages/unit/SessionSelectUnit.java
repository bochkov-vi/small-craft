package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import org.apache.wicket.model.IModel;

import java.util.Optional;

public class SessionSelectUnit extends SelectUnit implements IIdUnitSelect {

    public SessionSelectUnit(String id) {
        super(id);
    }

    public SessionSelectUnit(String id, IModel<Unit> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SessionUnitBehavior<>());

    }

    @Override
    public Long getIdUnit() {
        return getModel().map(Unit::getId).getObject();
    }

    @Override
    public void setIdUnit(Long id) {
        Unit unit = Optional.ofNullable(id).flatMap(pk -> repository.findById(pk)).orElse(null);
        setModelObject(unit);
    }
}
