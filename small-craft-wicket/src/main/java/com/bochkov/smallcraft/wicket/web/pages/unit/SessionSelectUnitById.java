package com.bochkov.smallcraft.wicket.web.pages.unit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class SessionSelectUnitById extends SelectUnitById implements ISessionUnitSelector {

    public SessionSelectUnitById(String id) {
        super(id);
    }

    public SessionSelectUnitById(String id, IModel<Long> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        setOutputMarkupId(true);
        add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getForm().visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
                    @Override
                    public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                        if (cmp instanceof SessionSelectUnitById) {
                            if (cmp.getOutputMarkupId()) {
                                target.add(cmp);
                            }
                        }
                    }
                });
            }
        });
        super.onInitialize();
    }


    @Override
    protected void onModelChanged() {
        Long unit = getModelObject();
        setAllFormModels(getModelObject());
    }

    public void setAllFormModels(Long unit) {
        setAllFormModels(unit, getForm());
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (getModel() != null) {
            if (!getModel().isPresent().getObject()) {
                getIdUnitFromSession().flatMap(id -> repository.findById(id)).ifPresent(u -> setModelObject(u.getId()));
            }
        }
    }

    @Override
    public void setIdUnitToModel(Long idUnit) {
        setModelObject(idUnit);
    }
}
