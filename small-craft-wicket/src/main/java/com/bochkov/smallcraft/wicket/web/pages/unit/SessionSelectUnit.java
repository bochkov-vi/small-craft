package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.wicket.component.select2.data.PersistableChoiceProvider;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import java.util.Optional;

public class SessionSelectUnit extends SelectUnit {

    public SessionSelectUnit(String id) {
        super(id);
    }

    public SessionSelectUnit(String id, IModel<Unit> model) {
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
                        if (cmp instanceof SessionSelectUnit) {
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
        Unit unit = getModelObject();
        if (unit != null && unit.getId() != null) {
            Session.get().setAttribute("id_unit", unit.getId());
            setAllFormModels(unit);
        }
    }

    public void setAllFormModels(Unit unit) {
        getForm().visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
            @Override
            public void component(FormComponent<?> cmp, IVisit<Object> visit) {
                if (cmp instanceof SessionSelectUnit) {
                    cmp.setDefaultModelObject(unit);
                }
            }
        });
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (getModel() != null) {
            if (!getModel().isPresent().getObject()) {
                Unit unit = Optional.ofNullable((Long) Session.get().getAttribute("id_unit"))
                        .flatMap(id -> repository.findById(id)).orElse(null);
                setModelObject(unit);
            }
        }
    }
}
