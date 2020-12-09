package com.bochkov.smallcraft.wicket.component;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FormComponentErrorBehavior extends Behavior implements Predicate<FeedbackMessage> {

    FormComponent formComponent;

    FeedbackMessage message = null;

    public static void appendFormComponent(FormComponent... components) {
        Stream.of(components).forEach(cmp -> {
            if (cmp.getBehaviors().stream().noneMatch(b -> b instanceof FormComponentErrorBehavior)) {
                cmp.add(new FormComponentErrorBehavior());
            }
        });
    }

    public static void append(MarkupContainer container) {
        container.visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
            @Override
            public void component(FormComponent formComponent, IVisit<Object> visit) {
                appendFormComponent(formComponent);
            }
        });
    }

    @Override
    public void bind(Component component) {
        formComponent = (FormComponent) component;
        formComponent.setOutputMarkupId(true);
        formComponent.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (message != null) {
                    oldClasses.add("is-invalid");
                    message = null;
                }
                return oldClasses;
            }
        });
    }

    @Override
    public void onConfigure(Component component) {
        message = getFirstFeedbackMessage();
        if (message != null && !message.isRendered()) {
            message.markRendered();
        }
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {

    }


    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        if (message != null && component.isVisibleInHierarchy()) {
            response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').closest('.form-group').append(\"<div class='invalid-feedback'>%s</div>\")",
                    component.getMarkupId(), String.valueOf(message.getMessage()))));
        }
    }

    @Override
    public boolean test(FeedbackMessage message) {
        return Objects.equals(message.getReporter(), formComponent) && (message.isWarning() || message.isError() || message.isFatal());
    }

    FeedbackMessage getFirstFeedbackMessage() {
        return formComponent.getFeedbackMessages().toList().stream().filter(this).findFirst().orElse(null);
    }
}
