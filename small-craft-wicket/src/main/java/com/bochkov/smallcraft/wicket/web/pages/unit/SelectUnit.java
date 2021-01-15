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
