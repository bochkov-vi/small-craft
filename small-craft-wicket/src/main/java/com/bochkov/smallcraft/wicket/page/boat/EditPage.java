package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("boat/edit")
public class EditPage extends CrudEditPage<Boat, String> {

    @SpringBean
    BoatRepository repository;

    public EditPage(PageParameters parameters) {
        super(parameters);
    }

    public EditPage(IModel<Boat> model) {
        super(model);
    }

    public EditPage() {
    }

    @Override
    protected Component createInputPanel(String id, IModel<Boat> model) {
        return new InputPanel(id, model);
    }

    @Override
    public BoatRepository getJpaRepository() {
        return repository;
    }

    @Override
    public Class<Boat> getEntityClass() {
        return Boat.class;
    }

}
