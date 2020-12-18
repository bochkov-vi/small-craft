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

public class SelectUnit extends Select2Choice<Unit> {

    @SpringBean
    UnitRepository repository;

    public SelectUnit(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectUnit(String id, IModel<Unit> model) {
        super(id, model);
        setProvider(provider());
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
                        if (cmp instanceof SelectUnit) {
                            if (cmp.getOutputMarkupId()) {
                                target.add(cmp);
                            }
                        }
                    }
                });
            }
        });
        getSettings().setPlaceholder(getString("unit"))
                .setCloseOnSelect(true)
                .setAllowClear(true)
                .setTheme("bootstrap4");

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
                if (cmp instanceof SelectUnit) {
                    cmp.setDefaultModelObject(unit);
                }
            }
        });
    }

    ChoiceProvider<Unit> provider() {
        ChoiceProvider<Unit> provider = PersistableChoiceProvider.of(Unit.class, (s, p) -> repository.findAll(s, p), "name", "id");
        return provider;
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
