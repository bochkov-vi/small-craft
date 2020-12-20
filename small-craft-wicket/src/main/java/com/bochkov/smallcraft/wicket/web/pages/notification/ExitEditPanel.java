package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.wicket.web.pages.exitnotification.EditPage;
import com.bochkov.wicket.data.model.PersistableModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

public class ExitEditPanel extends GenericPanel<Notification> {
    Component exitDate;

    @Inject
    ExitNotificationRepository exitNotificationRepository;
    private Label returnDate;
    private AjaxLink<Notification> btnExit;
    private AjaxLink<Notification> btnReturn;
    private AjaxLink<ExitNotification> btnEdit;

    public ExitEditPanel(String id) {
        super(id);
    }

    public ExitEditPanel(String id, IModel<Notification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        IModel<ExitNotification> exitNotificationIModel = LoadableDetachableModel.of(
                () -> getModel().map(notification -> exitNotificationRepository.findLast(notification).orElse(null)).getObject()
        );
        add(exitDate = new Label("exitDate", exitNotificationIModel.map(ExitNotification::getExitDateTime)) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(exitNotificationIModel.getObject() != null);
            }
        });
        add(returnDate = new Label("returnDate", exitNotificationIModel.map(ExitNotification::getReturnDateTime)) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(exitNotificationIModel.getObject() != null);
            }
        });
        add(btnExit = new AjaxLink<Notification>("btn-exit", getModel()) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(getModelObject().isValidExit(LocalDateTime.now()) && exitNotificationIModel.getObject().getReturnDateTime() != null);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                exitNotificationRepository.addCallExit(getModelObject(), LocalDateTime.now());
                target.add(ExitEditPanel.this);
            }
        });
        add(btnEdit = new AjaxLink<ExitNotification>("btn-edit", exitNotificationIModel) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(exitNotificationIModel.isPresent().getObject());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                EditPage editPage = new EditPage(PersistableModel.of(exitNotificationIModel.getObject(), id -> {
                    return exitNotificationRepository.findById(id);
                })) {
                    @Override
                    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<ExitNotification> model) {
                        super.onAfterSave(target, model);
                        setResponsePage(ExitEditPanel.this.getPage());
                    }
                };

                setResponsePage(editPage);
            }
        });
        add(btnReturn = new AjaxLink<Notification>("btn-return", getModel()) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(exitNotificationIModel.getObject() != null && exitNotificationIModel.getObject().getReturnDateTime() == null);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                exitNotificationRepository.addReturn(getModelObject());
                target.add(ExitEditPanel.this);
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

    }
}
