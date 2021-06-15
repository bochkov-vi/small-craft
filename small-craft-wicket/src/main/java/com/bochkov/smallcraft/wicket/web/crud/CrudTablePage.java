package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeLink;
import com.bochkov.wicket.component.table.XLSXDataExportLink;
import com.bochkov.wicket.data.provider.PersistableDataProvider;
import com.bochkov.wicket.jpa.model.CollectionModel;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


public abstract class CrudTablePage<T extends Persistable<ID>, ID extends Serializable> extends CrudPage<Collection<T>, T, ID> {


    protected WebMarkupContainer container = new WebMarkupContainer("container");

    protected ScrollToAnchorBehavior<T> scrollToAnchorBehavior;

    protected EntityDataTable<T, ID> table = createDataTable("table");

    XLSXDataExportLink exportExcel;

    @Getter
    private IModel<String> exportFileName;

    public CrudTablePage(Class<T> tClass) {
        super(tClass);
    }

    public CrudTablePage(Class<T> tClass, IModel<Collection<T>> model) {
        super(tClass, model);
    }

    public CrudTablePage(Class<T> tClass, PageParameters parameters) {
        super(tClass, parameters);
    }

    public static <T> Component createEditButton(String id, IModel<T> model, SerializableConsumer<IModel<T>> onEdit, Component parent) {
        AbstractLink button = null;
        button = new AuthorizeLink<T>(id, model) {
            @Override
            public void onClick() {
                onEdit.accept(model);
            }
        };
        button.setEscapeModelStrings(false);

        button.setBody(Model.of("<span class='fa fa-pencil'></span>"));
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-info");
                return oldClasses;
            }
        });
        button.add(new AttributeModifier("title", () -> parent.getString("edit")));
        return button;
    }

    protected EntityDataTable<T, ID> createDataTable(String compId) {
        return new EntityDataTable<T, ID>("table", columns(), provider()) {

            @Override
            public void onRowCreated(Item<T> row, String id, int index, IModel<T> model) {
                CrudTablePage.this.onRowCreated(table, row, id, index, model);
                //row.add(scrollToAnchorBehavior.classAttributeModifier(model, getModel()));
                row.add(scrollToAnchorBehavior.nameAttributeModifier(model));
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (getModel() == null) {
            setModel(CollectionModel.of(id -> getRepository().findById(id)));
        }
        scrollToAnchorBehavior = new ScrollToAnchorBehavior(entityClass);
        exportFileName = new ResourceModel("exportFileName").wrapOnAssignment(getPage());
        table.setOutputMarkupId(true);
        container.setOutputMarkupId(true);
        exportExcel = new XLSXDataExportLink("export-excel", table, exportFileName.getObject());
        container.add(table);
        container.setOutputMarkupId(true);
        container.add(createAddRowButton("btn-add-row"));
        container.add(exportExcel);
        add(scrollToAnchorBehavior);
        add(container);
    }

    protected ISortableDataProvider<T, String> provider() {
        return PersistableDataProvider.of(this::getRepository, this::specification, this::sort);
    }

    protected List<? extends IColumn<T, String>> columns() {
        List<? extends IColumn<T, String>> result = Lists.newArrayList();
        return result;
    }

    protected Specification<T> specification() {
        return null;
    }

    protected Sort sort() {
        return null;
    }

    public Component createEditButton(String id, IModel<T> model) {
        return createEditButton(id, model, this::onEdit, this);
    }

    public IColumn<T, String> createEditColumn() {
        return new HeaderlessColumn<T, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(createEditButton(componentId, rowModel));
            }
        };
    }

    public IColumn<T, String> createDeleteColumn() {
        return new HeaderlessColumn<T, String>() {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(createDeleteButton(componentId, rowModel, null));
            }
        };
    }

    public Component createAddRowButton(String id) {
        return new AuthorizeLink<Void>(id) {
            @Override
            public void onClick() {
                onAddRow(Optional.empty());
            }
        };
    }

    public void onEdit(IModel<T> model) {
        CrudEditPage<T, ID> page = createEditPage(model);
        setSelected(model);
        setResponsePage(page);
    }

    public void onAddRow(Optional<AjaxRequestTarget> target) {
        CrudEditPage editPage = createEditPage();
        editPage.addOnBack(CrudPage.goBackToPage(this));
        setResponsePage(editPage);
    }

    public abstract Class<? extends CrudEditPage<T, ID>> getEditPageClass();

    final private CrudEditPage<T, ID> createEditPage(IModel<T> model) {

        Class<? extends CrudEditPage<T, ID>> clazz = getEditPageClass();
        CrudEditPage<T, ID> page = null;
        try {
            PageParameters pageParameters = pageParametersForModel(model);
            Constructor<? extends CrudEditPage<T, ID>> constructor = null;
            constructor = clazz.getConstructor(PageParameters.class);

            page = BeanUtils.instantiateClass(constructor, pageParameters);
        } catch (NoSuchMethodException e) {
            page = createEditPage();
            page.setModel(model);
        }
        page.addOnBack(CrudPage.goBackToPage(this));
        scrollToAnchorBehavior.setAnchor(model);
        onEditPageCreated(page);
        return page;
    }

    public void onEditPageCreated(CrudEditPage<T, ID> page) {

    }

    private CrudEditPage<T, ID> createEditPage() {
        CrudEditPage<T, ID> page = BeanUtils.instantiateClass(getEditPageClass());
        return page;
    }

    @Override
    public void onDelete(AjaxRequestTarget target, IModel<T> model) {
        super.onDelete(target, model);
        target.add(table);
    }

    public void onRowCreated(EntityDataTable<T, ID> table, Item<T> item, final String id, final int index, final IModel<T> model) {

    }

    public CrudTablePage<T, ID> setSelectedCollection(IModel<Collection<T>> model) {
        scrollToAnchorBehavior.setAnchor(model.map(Collection::stream).map(Stream::findFirst).map(s -> s.orElse(null)));
        setModelObject(Lists.newArrayList(model.getObject()));
        return this;
    }

    public CrudTablePage<T, ID> setSelected(IModel<T> model) {
        scrollToAnchorBehavior.setAnchor(model);
        return this;
    }
}
