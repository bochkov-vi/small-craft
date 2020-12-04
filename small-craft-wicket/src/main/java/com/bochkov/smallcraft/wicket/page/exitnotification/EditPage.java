package com.bochkov.smallcraft.wicket.page.exitnotification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.service.ExitNotificationCustomSaveService;
import com.bochkov.smallcraft.wicket.page.crud.CrudEditPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Optional;

@MountPath("exit-notification/edit")
public class EditPage extends CrudEditPage<ExitNotification, Long> {

    @SpringBean
    ExitNotificationRepository repository;

    @SpringBean
    ExitNotificationCustomSaveService service;

    @Override
    public ExitNotification save(ExitNotification entity) {
        return service.save(entity);
    }

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
    protected void onInitialize() {
        super.onInitialize();
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
        ExitNotification notification = super.newEntityInstance();

        return notification;
    }


    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<ExitNotification> model) {
        ExitNotification notification = model.getObject();
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
