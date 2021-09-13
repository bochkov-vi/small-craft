package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.bootstrap.pagination.BootstrapPaginationToolbar;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;

public class EntityDataTable<ENTITY extends Persistable<ID>, ID extends Serializable> extends DataTable<ENTITY, String> {

    @Getter
    @Setter
    protected List<? extends AbstractToolbar> topToolbarsFirst;

    ISortableDataProvider<ENTITY, String> dataProvider;

    public EntityDataTable(String id, List<? extends IColumn<ENTITY, String>> iColumns, ISortableDataProvider<ENTITY, String> dataProvider, long rowsPerPage) {
        super(id, iColumns, dataProvider, rowsPerPage);
        this.dataProvider = dataProvider;
    }

    public EntityDataTable(String id, List<? extends IColumn<ENTITY, String>> iColumns, ISortableDataProvider<ENTITY, String> dataProvider) {
        this(id, iColumns, dataProvider, 50);
        this.dataProvider = dataProvider;
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (topToolbarsFirst != null) {
            topToolbarsFirst.forEach(this::addTopToolbar);
        }
        addTopToolbar(new TableStatisticToolbar(this));
        addTopToolbar(new BootstrapPaginationToolbar(this));
        addTopToolbar(new HeadersToolbar<String>(this, this.dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));
    }

    @Override
    protected final Item<ENTITY> newRowItem(String id, int index, IModel<ENTITY> model) {
        Item<ENTITY> item = super.newRowItem(id, index, model);
        onRowCreated(item, id, index, model);
        return item;
    }

    public void onRowCreated(Item<ENTITY> row, String id, int index, IModel<ENTITY> model) {

    }

    @Override
    public void addBottomToolbar(AbstractToolbar toolbar) {
        super.addBottomToolbar(toolbar);
    }

    public void addTopToolbar(AbstractToolbar toolbar, int index) {
        WebMarkupContainer container = getBottomToolbars();
        container.visitChildren(RepeatingView.class, (cmp, obj) -> {
            RepeatingView rv = (RepeatingView) cmp;

        });
    }
}
