package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeButton;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeLink;
import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Accessors(chain = true)
@AuthorizeInstantiation("ROLE_ADMIN")
public abstract class CrudEditPage<T extends Persistable<ID>, ID extends Serializable> extends CrudPage<T, T, ID> {

    static {
        PropertyUtils.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        /*ConvertUtils.register(new Converter() {
            @Override
            public <T> T convert(Class<T> type, Object value) {
                return (T) Sets.newHashSet((Collection) value);
            }
        }, Set.class);*/
    }

    @SpringBean
    EntityManager entityManager;

    WebMarkupContainer container = new WebMarkupContainer("container");

    @Getter
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
        Button button = new AuthorizeButton(id) {
            @Override
            public void onSubmit() {
                onSave(Optional.empty(), CrudEditPage.this.getModel());
            }
        };
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

    }

    public void onSaveError(Optional<AjaxRequestTarget> target, IModel<T> model) {
        error(getString("save.error.unknown"));
        target.ifPresent(t -> t.add(feedback));
    }

    public void onClone(Optional<AjaxRequestTarget> target, IModel<T> model) {
        IModel<T> newModel = createModelForNewRow(() -> {
            T clone = newEntityInstance();
            T src = model.getObject();
            copyDataForClone(src, clone);
            return clone;
        });
        CrudEditPage<T, ID> editPage = BeanUtils.instantiateClass(getClass());
        editPage.setModel(newModel);
        editPage.setResponsePage(getPage());
        Page _this = this;
        editPage.addOnBack((SerializableConsumer<IModel<T>>) tiModel -> setResponsePage(_this));
        if (target.isPresent()) {
            target.get().add(editPage);
        } else {
            setResponsePage(editPage);
        }

    }

    public void onAddRow() {
        CrudEditPage<T, ID> page = BeanUtils.instantiateClass(this.getClass());
        page.addOnBack(this.getOnBack());
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
        form.add(createBackButton("btn-back"));
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
        return createModelForNewRow(this::newEntityInstance);
    }

    final protected IModel<T> createModelForNewRow(SerializableSupplier<T> newInstanceCreator) {
        IModel<T> model = PersistableModel.of(getRepository()::findById, newInstanceCreator);
        return model;
    }


    public T copyDataForClone(final T src, final T dst) {
        try {
            entityManager.detach(dst);
            org.apache.commons.beanutils.BeanUtils.copyProperties(dst, src);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return dst;
    }

    public AbstractLink createCloneButton(String id, IModel<T> model) {
        AbstractLink button = new AuthorizeLink<T>(id, model) {
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


    public Component createAddRowButton(String id) {
        return new AuthorizeLink<Void>(id) {
            @Override
            public void onClick() {
                onAddRow();
            }
        };
    }

    @Override
    public void onAfterDelete(AjaxRequestTarget target) {
        super.onAfterDelete(target);
        if (getOnBack() != null) {
            getOnBack().accept(null);
        }
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }
}
