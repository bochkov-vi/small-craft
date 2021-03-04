package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.wicket.component.InputMaskBehavior;
import com.bochkov.wicket.jpa.model.CollectionModel;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

@Accessors(chain = true)
public class FormComponentInput extends FormComponentPanel<Unit> {

    @Inject
    UnitRepository repository;

    @Getter
    @Setter
    boolean canSelect = false;

    IModel<Unit> selectedEntity = PersistableModel.of(repository::findById);

    FormComponent<String> name = new TextField<>("name", Model.of(), String.class).setRequired(true);

    FormComponent<String> phone = new TextField<>("phone", Model.of(), String.class).setRequired(true);


    FormComponent<Unit> select = new SessionSelectUnit("select", selectedEntity);

    FormComponent<Unit> id = new HiddenField<Unit>("id", selectedEntity, Unit.class);

    FormComponent<Collection<Unit>> parents = new SelectMultiUnit("parents", CollectionModel.of(id -> repository.findById(id)));

    public FormComponentInput(String id, IModel<Unit> model) {
        super(id, model);
    }

    public FormComponentInput(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        select.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(FormComponentInput.this);
                setModelObject(select.getModelObject());
            }
        });
        add(name);
        add(select, id, parents, phone);
        FormComponentErrorBehavior.append(this);
    }

    @Override
    protected void onBeforeRender() {
        Unit unit = getModelObject();
        select.setModelObject(unit);
        phone.add(InputMaskBehavior.phone());
        phone.setModelObject(unit.getPhone());
        name.setModelObject(getModel().map(Unit::getName).getObject());
        parents.setModelObject(getModel().map(Unit::getParents).getObject());
        super.onBeforeRender();
    }

    @Override
    public void convertInput() {
        Unit unit = select.getModelObject();
        if (unit == null) {
            unit = new Unit();
        }
        unit.setPhone(phone.getConvertedInput());
        unit.setName(name.getConvertedInput());
        unit.setParents(Optional.ofNullable(parents.getConvertedInput()).map(Lists::newArrayList).orElse(null));
        setConvertedInput(unit);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (canSelect) {
            select.setVisible(true);
            id.setVisible(false);
            select.setEnabled(true);
            id.setEnabled(false);

        } else {
            select.setVisible(false);
            id.setVisible(true);
            select.setEnabled(false);
            id.setEnabled(true);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

    }
}
