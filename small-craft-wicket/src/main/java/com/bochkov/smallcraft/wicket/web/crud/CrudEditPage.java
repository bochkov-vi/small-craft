package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.wicket.data.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Optional;

@Accessors(chain = true)
public abstract class CrudEditPage<T extends Persistable<ID>, ID extends Serializable> extends CrudPage<T, T, ID> {

    WebMarkupContainer container = new WebMarkupContainer("container");

    Form<T> form = new Form<>("form");



    public CrudEditPage(Class<T> entityClass, PageParameters parameters) {
        super(entityClass, parameters);
        T entity = getConverter(getEntityClass()).convertToObject(parameters.get(0).toOptionalString(), Session.get().getLocale());
        setModel(createModelForNewRow());
        if (entity != null) {
            setModelObject(entity);
        }
    }

    public CrudEditPage(Class<T> entityClass, IModel<T> model) {
        super(entityClass, model);
    }

    public CrudEditPage(Class<T> entityClass) {
        super(entityClass);
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
            internalSave(model.getObject());
            String message = new StringResourceModel("save.success", form, model).setParameters(model.getObject()).getObject();
            Session.get().success(message);
            onAfterSave(target, model);
        } catch (NestedRuntimeException ex) {
            String message = new StringResourceModel("save.error", form, model).setParameters(model.getObject()).getObject();
            Session.get().error(message);
            Session.get().fatal(((NestedRuntimeException) ex).getMostSpecificCause());
            log.error(message, ex);
        } catch (CrudIteruptException ex) {
        } catch (Exception ex) {
            String message = new StringResourceModel("save.error", form, model).setParameters(model.getObject()).getObject();
            Session.get().error(message);
            Session.get().fatal(ex);
            log.error(message, ex);
        }
        target.ifPresent(t -> t.add(feedback));
    }

    public final T internalSave(T entity) {
        T saved = save(entity);
        setModelObject(saved);
        return saved;
    }

    public T save(T entity) {
        return getRepository().save(entity);
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
        target.ifPresent(t -> t.add(feedback));
        CrudEditPage<T, ID> editPage = BeanUtils.instantiateClass(getClass());

    }

    public void onAddRow(Optional<AjaxRequestTarget> target) {
        CrudEditPage<T, ID> page = BeanUtils.instantiateClass(this.getClass());
        page.setBackPage(getBackPage());
        setResponsePage(page);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        IModel<T> model = getModel();
        if (model == null) {
            setModel(createModelForNewRow());
        }
        form.setModel(new CompoundPropertyModel<>(getModel()));
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


    public T newEntityInstance() {
        return BeanUtils.instantiateClass(getEntityClass());
    }

    final protected IModel<T> createModelForNewRow() {
        IModel<T> model = PersistableModel.of(getRepository()::findById, () -> newEntityInstance());
        return model;
    }

    private AbstractLink createCloneButton(String id, IModel<T> model) {
        AbstractLink button = null;
        if (ajax) {
            button = createAjaxCloneButton(id, model);
        } else {
            button = createSimpleCloneButton(id, model);
        }

        return button;
    }

    public AbstractLink createAjaxCloneButton(String id, IModel<T> model) {
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

    public AbstractLink createSimpleCloneButton(String id, IModel<T> model) {
        AbstractLink button = new Link<T>(id, model) {
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
        button.setVisible(false).setEnabled(false);
        return button;
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

    @Override
    public void onAfterDelete(AjaxRequestTarget target) {
        super.onAfterDelete(target);
        if (backPage != null) {
            setResponsePage(backPage);
        }
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
