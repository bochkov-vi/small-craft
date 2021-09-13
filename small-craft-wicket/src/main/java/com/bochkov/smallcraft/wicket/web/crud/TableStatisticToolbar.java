package com.bochkov.smallcraft.wicket.web.crud;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class TableStatisticToolbar extends AbstractToolbar {

    IModel<Long> cntModel = LoadableDetachableModel.of(() -> getTable().getRowCount());

    Label cnt = new Label("cnt", cntModel);

    public TableStatisticToolbar(IModel<?> model, DataTable<?, ?> table) {
        super(model, table);
    }

    public TableStatisticToolbar(DataTable<?, ?> table) {
        super(table);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        cnt.add(new AttributeModifier("colspan", LoadableDetachableModel.of(() -> getTable().getColumns().size())));
        add(cnt);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        cnt.setVisible(cntModel.getObject() != null && cntModel.getObject() > 0);
    }
}
