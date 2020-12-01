package com.bochkov.smallcraft.wicket.component;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class FormGroupBorder extends Border implements Predicate<FeedbackMessage> {

    Label feedbackPanel;

    FormComponent formComponent;

    public FormGroupBorder(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        formComponent = (FormComponent) this.streamChildren().filter(input -> input instanceof FormComponent).findFirst().orElse(null);
        feedbackPanel = new Label("feedback", Model.of());
        formComponent.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (formComponent.isValid()) {
                    //oldClasses.add("is-valid");
                } else {
                    oldClasses.add("is-invalid");
                }
                return oldClasses;
            }
        });
        feedbackPanel.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (!formComponent.getFeedbackMessages().isEmpty()) {
                    oldClasses.add("invalid-feedback");
                } else {
                    oldClasses.add("d-none");
                }
                return oldClasses;
            }
        });
        addToBorder(feedbackPanel);
    }

    @Override
    public boolean test(FeedbackMessage message) {
        return Objects.equals(message.getReporter(), formComponent) && (message.isWarning() || message.isError() || message.isFatal());
    }

    FeedbackMessage getFirstFeedbackMessage() {
        return formComponent.getFeedbackMessages().toList().stream().filter(this).findFirst().orElse(null);
    }

    @Override
    protected void onConfigure() {

        FeedbackMessage message = getFirstFeedbackMessage();
        if (message != null) {
            message.markRendered();
            feedbackPanel.setDefaultModelObject(message.getMessage());
        }else {
            feedbackPanel.setDefaultModelObject(null);
        }
        super.onConfigure();
    }


}
