package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Optional;

public class SessionSelectUnit extends SelectUnit implements ISessionUnitSelector {

    public SessionSelectUnit(String id) {
        super(id);
    }

    public SessionSelectUnit(String id, IModel<Unit> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getForm().visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
                    @Override
                    public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                        if (cmp instanceof ISessionUnitSelector) {
                            if (cmp.getOutputMarkupId()) {
                                target.add(cmp);
                            }
                        }
                    }
                });
            }
        });
        if (getModel() != null) {
            if (!getModel().isPresent().getObject()) {
                getIdUnitFromSession().flatMap(id -> repository.findById(id)).ifPresent(this::setModelObject);
            }
        }

    }

    @Override
    protected void onModelChanged() {
        setAllFormModels(getModel().map(Unit::getId).getObject(), getForm());
    }


    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

    }

    @Override
    public void setIdUnitToModel(Long idUnit) {
        setModelObject(Optional.ofNullable(idUnit).flatMap(id->repository.findById(id)).orElse(null));
    }
}
