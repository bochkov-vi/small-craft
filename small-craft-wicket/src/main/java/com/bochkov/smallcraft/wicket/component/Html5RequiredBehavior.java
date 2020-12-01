package com.bochkov.smallcraft.wicket.component;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.stream.Stream;

public class Html5RequiredBehavior extends Behavior {
    public static void appendFormComponent(FormComponent... components) {
        Stream.of(components).forEach(cmp -> {
            if (cmp.getBehaviors().stream().noneMatch(b -> b instanceof Html5RequiredBehavior)) {
                cmp.add(new Html5RequiredBehavior());
            }
        });
    }


    public static void append(MarkupContainer form) {
        form.visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
            @Override
            public void component(FormComponent cmp, IVisit<Object> visit) {
                appendFormComponent(cmp);
            }
        });
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        FormComponent formComponent = (FormComponent) component;
        if (formComponent.isRequired()) {
            tag.put("required", true);
        }
    }
}
