package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class ExitEditPanel extends GenericPanel<Notification> {
    Component exitDate;

    @Inject
    ExitNotificationRepository exitNotificationRepository;
    private Label returnDate;
    private AjaxLink<Notification> btnExit;
    private AjaxLink<Notification> btnReturn;

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
        add(returnDate =new Label("returnDate", exitNotificationIModel.map(ExitNotification::getReturnDateTime)) {
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
