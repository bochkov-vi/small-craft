package com.bochkov.smallcraft.wicket.component.filter;

import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnitById;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Objects;

public class FilterPanel<T> extends Border {

    Form<T> form = new Form("form");

    public FilterPanel(String id) {
        super(id);
    }

    public FilterPanel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addToBorder(form);
        form.setModel(new CompoundPropertyModel(this.getDefaultModel()));
        form.add(new TextField<>("quickSearch"));
        form.add(new SessionSelectUnitById("unit"));
        form.add(new Button("clear-filter") {
            @Override
            public void onSubmit() {
                form.clearInput();
                Object data = form.getModel();
                form.visitFormComponents((comp, visit) -> {
                    if (Objects.equals("includeUnitChilds", comp.getId())) {
                        return;
                    }
                    if (Objects.equals("unit", comp.getId())) {
                        return;
                    }
                    if (comp.getModel() != null) {
                        comp.setDefaultModelObject(null);
                    }
                });
            }
        });
        form.add(new CheckBox("includeUnitChilds").setOutputMarkupId(true));
    }
}
