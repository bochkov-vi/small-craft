package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.wicket.select2.data.PersistableChoiceProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2MultiChoice;

import java.util.Collection;

public class SelectMultiUnit extends Select2MultiChoice<Unit> {

    @SpringBean
    UnitRepository repository;

    public SelectMultiUnit(String id) {
        super(id);
        setProvider(provider());
    }

    public SelectMultiUnit(String id, IModel<Collection<Unit>> model) {
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
                        if (cmp instanceof SelectMultiUnit) {
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

    ChoiceProvider<Unit> provider() {
        ChoiceProvider<Unit> provider = PersistableChoiceProvider.of(Unit.class, (s, p) -> repository.findAll(s, p), "name", "id");
        return provider;
    }

}
