package com.bochkov.smallcraft.wicket.web.pages.unit;

import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectHierachicalUnitById extends FormComponent<Collection<Long>> {

    SelectUnitById unit = new SelectUnitById("select", Model.of());

    CheckBox checkBox = new CheckBox("checkbox", Model.of(Boolean.FALSE));

    @SpringBean
    UnitRepository unitRepository;

    public SelectHierachicalUnitById(String id) {
        super(id);
    }

    public SelectHierachicalUnitById(String id, IModel<Collection<Long>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(unit, checkBox);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    @Override
    public void convertInput() {
        super.convertInput();
        Optional<Unit> unitOptional = Optional.ofNullable(unit.getModelObject()).flatMap(unitRepository::findById);
        if (checkBox.getModel().orElse(false).getObject()) {
            unitOptional.map(u -> u.getAllChildsAndThis().stream().map(Unit::getId).collect(Collectors.toList())).ifPresent(this::setConvertedInput);
        } else {
            unitOptional.map(Unit::getId).ifPresent(id -> setConvertedInput(Lists.newArrayList(id)));
        }
    }
}
