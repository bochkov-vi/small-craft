package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.wicket.web.pages.boat.component.ChoiseEnumInput;
import com.bochkov.smallcraft.wicket.web.pages.filter.Filter;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.springframework.data.jpa.domain.Specification;

public class BoatFilterPanel extends GenericPanel<Filter<Boat>> {


    public BoatFilterPanel(String id) {
        super(id);
    }

    public BoatFilterPanel(String id, IModel<Filter<Boat>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        Form<Filter<Boat>> form = new Form<>("form", new CompoundPropertyModel<>(getModel()));
        add(form);
        form.add(new TextField<>("quickSearch", String.class));
        form.add(new SessionSelectUnit("unit"));
        FormComponent<BoatFilter.Expirated> expiratedDropDownChoice = new ChoiseEnumInput<BoatFilter.Expirated>("expire",BoatFilter.Expirated.class);
        form.add(expiratedDropDownChoice);
        super.onInitialize();
    }

    public Specification<Boat> specification() {
        return getModel().map(Filter::specification).orElse(null).getObject();
    }
}
