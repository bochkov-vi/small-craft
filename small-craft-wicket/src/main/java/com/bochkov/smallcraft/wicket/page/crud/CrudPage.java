package com.bochkov.smallcraft.wicket.page.crud;

import com.bochkov.smallcraft.wicket.page.BasePage;
import com.bochkov.wicket.data.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.Set;

@Accessors(chain = true)
public abstract class CrudPage<T, ENTITY extends Persistable<ID>, ID extends Serializable> extends BasePage<T> {

    @Getter
    @Setter
    protected Page backPage;

    protected DeletePanel<ENTITY, ID> deletePanel = new DeletePanel<ENTITY, ID>("deleted-panel") {
        @Override
        public void onDelete(AjaxRequestTarget target, IModel model) {
            CrudPage.this.onDelete(target, model);
        }
    };

    protected org.slf4j.Logger log;

    protected FeedbackPanel feedback = new FeedbackPanel("feedback");

    protected Class<ENTITY> entityClass;

    public CrudPage(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }

    public CrudPage() {
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
        entityClass = (Class<ENTITY>) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }

    public CrudPage(IModel<T> model) {
        super(model);
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
        entityClass = (Class<ENTITY>) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }

    public CrudPage(PageParameters parameters) {
        super(parameters);
        log = org.slf4j.LoggerFactory.getLogger(this.getClass());
        entityClass = (Class<ENTITY>) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }

    protected abstract <R extends JpaRepository<ENTITY, ID>> R getJpaRepository();

    public void onDelete(AjaxRequestTarget target, IModel<ENTITY> model) {
        deletePanel.hide(target);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        deletePanel.setDeletedEntityModel(PersistableModel.of(id -> getJpaRepository().findById(id)));
        deletePanel.add(createDetails("details", deletePanel.deletedEntityModel));
        deletePanel.setDefaultModel(Model.of());
        add(feedback);
        add(deletePanel);
    }

    public AbstractLink createBackButton(String id, boolean ajax) {
        AbstractLink button = null;
        if (ajax) {
            button = createAjaxBackButton(id);
        } else {
            button = createSimpleBackButton(id);
        }
        button.setEscapeModelStrings(false);
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-info");
                return oldClasses;
            }
        });
        button.setBody(Model.of("<span class='fa fa-mail-reply'></span>"));
        return button;
    }

    private AbstractLink createAjaxBackButton(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onBack(Optional.of(target));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(backPage != null);
            }
        };
    }

    private AbstractLink createSimpleBackButton(String id) {
        return new Link<Void>(id) {

            @Override
            public void onClick() {
                onBack(Optional.empty());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                this.setVisible(backPage != null);
            }
        };
    }

    public void onBack(Optional<AjaxRequestTarget> target) {
        if (backPage != null) {
            setResponsePage(backPage);
        }
    }

    public void onRequestDelete(AjaxRequestTarget target, IModel<ENTITY> model) {
        deletePanel.deletedEntityModel.setObject(model.getObject());
        deletePanel.setDefaultModelObject(getString("deleteDialogHeader"));
        deletePanel.show(target);
    }

    public final Component createDeleteButton(String id, IModel model) {
        AbstractLink button = null;

        button = createDeleteAjaxButton(id, model);

        button.setEscapeModelStrings(false);
        button.setBody(Model.of("<span class='fa fa-trash'></span>"));
        button.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("btn");
                oldClasses.add("btn-outline-danger");
                return oldClasses;
            }
        });
        button.add(new DisabledAttributeBehavior());
        return button;
    }

    public final AbstractLink createDeleteAjaxButton(String id, IModel<ENTITY> model) {
        AjaxLink link = new AjaxLink<ENTITY>(id, model) {
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


    public Component createDetails(String id, IModel<ENTITY> entityiModel) {
        return new DefaultDetailsPanel<ENTITY>(id, entityiModel, entityClass);
    }
}
