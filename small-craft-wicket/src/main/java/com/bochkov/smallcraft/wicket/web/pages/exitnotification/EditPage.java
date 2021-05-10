package com.bochkov.smallcraft.wicket.web.pages.exitnotification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.Optional;

@MountPath("exit-notification/edit")
public class EditPage extends CrudEditPage<ExitNotification, Long> {

    @Inject
    ExitNotificationRepository repository;

    public EditPage(PageParameters parameters) {
        super(ExitNotification.class, parameters);

    }


    public EditPage(IModel<ExitNotification> model) {
        super(ExitNotification.class, model);
    }

    public EditPage() {
        super(ExitNotification.class);
    }

    @Override
    public ExitNotification save(ExitNotification entity) {
        return repository.safeSave(entity);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.feedback.setEscapeModelStrings(false);
    }

    @Override
    protected Component createInputPanel(String id, IModel<ExitNotification> model) {
        return new InputPanel(id, model);
    }

    @Override
    public ExitNotificationRepository getRepository() {
        return repository;
    }

    @Override
    public ExitNotification newEntityInstance() {
        ExitNotification exit = super.newEntityInstance();
        return exit;
    }


    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<ExitNotification> model) {
        ExitNotification exitNotification = model.getObject();
        super.onSave(target, model);
    }

    @Override
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<ExitNotification> model) {

    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }


}
