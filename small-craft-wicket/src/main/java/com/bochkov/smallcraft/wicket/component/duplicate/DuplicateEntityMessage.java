package com.bochkov.smallcraft.wicket.component.duplicate;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class DuplicateEntityMessage<T> extends FeedbackMessage {
    IModel<T> duplicateModel;

    public DuplicateEntityMessage(Component reporter, Serializable message, int level, IModel<T> duplicateModel) {
        super(reporter, message, level);
        this.duplicateModel = duplicateModel;
    }

    @Override
    public void detach() {
        super.detach();
    }
}
