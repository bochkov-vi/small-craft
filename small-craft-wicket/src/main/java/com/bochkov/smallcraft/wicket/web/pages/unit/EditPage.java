package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("unit/edit")
public class EditPage extends CrudEditPage<Unit, Long> {

    @SpringBean
    UnitRepository repository;

    public EditPage(PageParameters parameters) {
        super(Unit.class, parameters);
    }

    public EditPage(IModel<Unit> model) {
        super(Unit.class, model);
    }

    public EditPage() {
        super(Unit.class);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Unit> model) {
        return new InputPanel(id, model);
    }

    @Override
    public UnitRepository getRepository() {
        return repository;
    }

}
