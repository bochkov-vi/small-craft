package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.DuplicateError;
import com.bochkov.smallcraft.wicket.web.BasePage;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeAjaxLink;
import com.bochkov.smallcraft.wicket.web.crud.button.AuthorizeLink;
import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Set;

@Accessors(chain = true)
public abstract class CrudPage<T, ENTITY extends Persistable<ID>, ID extends Serializable> extends BasePage<T> {

    protected org.slf4j.Logger log;

    protected FeedbackPanel feedback = new FeedbackPanel("feedback", new IFeedbackMessageFilter() {
        @Override
        public boolean accept(FeedbackMessage message) {
            return !message.isRendered() && !(message.getMessage() instanceof DuplicateError) && !FormComponentErrorBehavior.canRender(message);
        }
    });

    protected DeletePanel<ENTITY, ID> deletePanel = new DeletePanel<ENTITY, ID>("deleted-panel") {
        @Override
        public void onDelete(AjaxRequestTarget target, IModel model) {
            CrudPage.this.onDelete(target, model);
        }
    };

    protected Class<ENTITY> entityClass;

    @Getter
    private SerializableConsumer<IModel<T>> onBack = null;

    public CrudPage(Class<ENTITY> entityClass) {
        super();
        this.entityClass = entityClass;
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    public CrudPage(Class<ENTITY> entityClass, IModel<T> model) {
        super(model);
        this.entityClass = entityClass;
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    public CrudPage(Class<ENTITY> entityClass, PageParameters parameters) {
        super(parameters);
        this.entityClass = entityClass;
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    public static <E> SerializableConsumer<IModel<E>> goBackToPage(Page backPage) {
        return new SerializableConsumer<IModel<E>>() {
            @Override
            public void accept(IModel<E> tiModel) {
                RequestCycle.get().setResponsePage(backPage);
            }
        };
    }

    public static <X> PageParameters pageParametersForModel(IModel<X> model) {
        return pageParameters(model.getObject());
    }

    public static <X> PageParameters pageParameters(X object) {
        PageParameters parameters = new PageParameters();
        Class<X> clazz = (Class<X>) object.getClass();
        String value = Application.get().getConverterLocator().getConverter(clazz).convertToString(object, Session.get().getLocale());
        parameters.set(0, value);
        return parameters;
    }

    protected abstract <R extends CrudRepository<ENTITY, ID>> R getRepository();

    public void onDelete(AjaxRequestTarget target, IModel<ENTITY> model) {

        if (model != null) {
            target.add(feedback);
            ENTITY entity = model.getObject();
            if (entity != null && !entity.isNew()) {
                try {
                    getRepository().delete(model.getObject());
                    deletePanel.hide(target);
                    info(new StringResourceModel("delete.success", this, model).setParameters(entity).getObject());
                    onAfterDelete(target);
                } catch (Exception e) {
                    String message = new StringResourceModel("delete.error", this, model).setParameters(model.getObject()).getObject();
                    error(((NestedRuntimeException) e).getMostSpecificCause());
                    log.error(message, e);
                }
            } else {
                error(new StringResourceModel("delete.empty.error", this, model).setParameters(model.getObject()).getObject());
            }
        }
    }

    public void onAfterDelete(AjaxRequestTarget target) {

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        deletePanel.setDeletedEntityModel(PersistableModel.of(id -> getRepository().findById(id)));
        deletePanel.add(createDetails("details", deletePanel.deletedEntityModel));
        deletePanel.setDefaultModel(Model.of());
        add(feedback);
        feedback.setOutputMarkupId(true);
        add(deletePanel);
    }

    public AbstractLink createBackButton(String id) {
        return new AuthorizeLink<T>(id, getModel()) {
            @Override
            public void onClick() {
                if (onBack != null) {
                    onBack.accept(getModel());
                }
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(onBack != null);
            }
        };
    }

    public void onRequestDelete(AjaxRequestTarget target, IModel<ENTITY> model) {
        deletePanel.deletedEntityModel.setObject(model.getObject());
        deletePanel.setDefaultModelObject(getString("deleteDialogHeader"));
        deletePanel.show(target);
    }

    public Component createDeleteButton(String id, IModel<ENTITY> model, IModel<String> label) {
        AbstractLink button = null;

        button = createDeleteButton(id, model);
        button.add(new AttributeAppender("title", new ResourceModel("delete").wrapOnAssignment(button)));
        button.setEscapeModelStrings(false);
        if (label != null) {
            button.setBody(label.map(str -> String.format("<span class='fa fa-trash'></span><span>%s</span", str)));
        } else {
            button.setBody(Model.of("<span class='fa fa-trash'></span>"));
        }
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-danger");
                return oldClasses;
            }
        });

        return button;
    }

    public final AbstractLink createDeleteButton(String id, IModel<ENTITY> model) {
        AjaxLink link = new AuthorizeAjaxLink<ENTITY>(id, model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onRequestDelete(target, model);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                ENTITY e = getModelObject();
                this.setEnabled(e != null && !e.isNew());
            }
        };

        return link;
    }

    public final Class<ENTITY> getEntityClass() {
        return entityClass;
    }

    public Component createDetails(String id, IModel<ENTITY> entityiModel) {
        return new DefaultDetailsPanel<ENTITY>(id, entityiModel, entityClass);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    public void addOnBack(SerializableConsumer<IModel<T>> consumer) {
        this.onBack = onBack != null ? onBack.andThen(consumer) : consumer;
    }

    public void setBackPage(Page page) {
        addOnBack(goBackToPage(page));
    }
}
