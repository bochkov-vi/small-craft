package com.bochkov.smallcraft.wicket.page.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Objects;
import java.util.Optional;

@MountPath("boat/edit")
public class EditPage extends CrudEditPage<Boat, Long> {

    @SpringBean
    BoatRepository repository;

    public EditPage(PageParameters parameters) {
        super(Boat.class, parameters);
    }

    public EditPage(IModel<Boat> model) {
        super(Boat.class, model);
    }

    public EditPage() {
        super(Boat.class);
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
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Boat> model) {
        if (model.map(Boat::getOwn).filter(Objects::nonNull).map(person -> Boolean.TRUE).orElse(Boolean.FALSE).getObject()) {
            super.onAfterSave(target, model);
        }
    }
}
