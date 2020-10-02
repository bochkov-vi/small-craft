package com.bochkov.smallcraft.wicket.page.crud;

import com.bochkov.wicket.data.model.PersistableModel;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Optional;

public abstract class CrudEditPage<T extends Persistable<ID>, ID extends Serializable> extends CrudPage<T, T, ID> {

    WebMarkupContainer container = new WebMarkupContainer("container");

    Form<T> form = new Form<>("form");


    boolean ajax = false;

    public CrudEditPage(PageParameters parameters) {
        super(parameters);

        T entity = getConverter(getEntityClass()).convertToObject(parameters.get(0).toOptionalString(), Session.get().getLocale());
        setModel(PersistableModel.of(id -> getJpaRepository().findById(id)));
        setModelObject(entity);
    }

    public CrudEditPage(IModel<T> model) {
        super(model);
    }

    public CrudEditPage() {
        super();
    }

    public CrudEditPage(Class<T> tClass) {
        super(tClass);
    }

    private Button createSaveButton(String id) {
        Button button = null;
        if (ajax) {
            button = createAjaxSaveButton(id);
        } else {
            button = createSimpleSaveButton(id);
        }
        return button;
    }

    private Button createAjaxSaveButton(String id) {
        return new AjaxButton(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                onSave(Optional.of(target), CrudEditPage.this.getModel());
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                onSaveError(Optional.of(target), CrudEditPage.this.getModel());
            }
        };
    }

    private Button createSimpleSaveButton(String id) {
        return new Button(id) {
            @Override
            public void onSubmit() {
                onSave(Optional.empty(), CrudEditPage.this.getModel());
            }
        };
    }


    public void onSave(Optional<AjaxRequestTarget> target, IModel<T> model) {
        try {
            getJpaRepository().save(getModelObject());
            String message = MessageFormat.format(getString("save.success"), model.getObject());
            success(message);
            onAfterSave(target, model);
        } catch (Exception e) {
            String message = MessageFormat.format(getString("save.error"), model.getObject());
            error(message);
            error(((NestedRuntimeException) e).getMostSpecificCause());
            log.error(message, e);
        }
        target.ifPresent(t -> t.add(feedback));
    }

    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<T> model) {
        if (backPage != null) {
            setResponsePage(backPage);
        }
    }

    public void onSaveError(Optional<AjaxRequestTarget> target, IModel<T> model) {
        error(getString("save.error.unknown"));
        target.ifPresent(t -> t.add(feedback));
    }

    public void onClone(Optional<AjaxRequestTarget> target, IModel<T> model) {
        success("Object cloned!!!");
        target.ifPresent(t -> t.add(feedback));
    }

    @Override
    public void onDelete(AjaxRequestTarget target, IModel<T> model) {
        error("Object deleted!!!");
        target.add(feedback);
    }

    public void onAddRow(Optional<AjaxRequestTarget> target) {
        setResponsePage(BeanUtils.instantiateClass(this.getClass()));
    }

    public abstract Class<T> getEntityClass();

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<T> model = getModel();
        if (model == null) {
            setModel(createModelForNewRow());
        }
        form.setModel(getModel());
        Button saveButton = createSaveButton("btn-save");
        form.add(saveButton);

        AbstractLink cloneButton = createCloneButton("btn-clone", getModel());
        form.add(cloneButton);

        Component addButton = createAddRowButton("btn-new");
        form.add(addButton);

        Component deleteButton = createDeleteButton("btn-delete", getModel());
        form.add(deleteButton);
        form.add(createBackButton("btn-back", ajax));
        form.add(createInputPanel("input-panel", getModel()));
        form.setDefaultButton(saveButton);
        container.add(form);
        container.setOutputMarkupId(true);
        add(container);
    }

    protected abstract Component createInputPanel(String id, IModel<T> model);

    public abstract <R extends JpaRepository<T, ID>> R getJpaRepository();

    public T newEntityInstance() {
        return BeanUtils.instantiateClass(getEntityClass());
    }

    protected IModel<T> createModelForNewRow() {
        IModel<T> model = PersistableModel.of(getJpaRepository()::findById, () -> newEntityInstance());
        return model;
    }

    private AbstractLink createCloneButton(String id, IModel<T> model) {
        AbstractLink button = null;
        if (ajax) {
            button = createAjaxCloneButton(id, model);
        } else {
            button = createSimpleCloneButton(id, model);
        }
        button.add(new DisabledAttributeBehavior());
        return button;
    }

    private AbstractLink createAjaxCloneButton(String id, IModel<T> model) {
        return new AjaxLink<T>(id, model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onClone(Optional.of(target), CrudEditPage.this.getModel());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                T object = getModelObject();
                setEnabled(object != null && !object.isNew());
            }
        };
    }

    private AbstractLink createSimpleCloneButton(String id, IModel<T> model) {
        return new Link<T>(id, model) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                T object = getModelObject();
                setEnabled(object != null && !object.isNew());
            }

            @Override
            public void onClick() {
                onClone(Optional.empty(), CrudEditPage.this.getModel());
            }
        };
    }


    public Component createAddRowAjaxButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAddRow(Optional.of(target));
            }
        };
    }

    public Component createAddRowSimpleButton(String id) {
        return new Link<Void>(id) {
            @Override
            public void onClick() {
                onAddRow(Optional.empty());
            }
        };
    }

    public Component createAddRowButton(String id) {
        if (ajax) {
            return createAddRowAjaxButton(id);
        } else {
            return createAddRowSimpleButton(id);
        }
    }

}
