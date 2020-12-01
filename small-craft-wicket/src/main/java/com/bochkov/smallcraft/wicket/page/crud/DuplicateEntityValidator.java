package com.bochkov.smallcraft.wicket.page.crud;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.data.domain.Persistable;

import java.util.Objects;

public abstract class DuplicateEntityValidator<T extends Persistable, S> implements IValidator<S> {

    FormComponent<S> formComponent;

    Form<T> form;

    public DuplicateEntityValidator(FormComponent<S> formComponent, Form<T> form) {
        this.formComponent = formComponent;
        this.form = form;
    }

    @Override
    public void validate(IValidatable<S> validatable) {
        S value = validatable.getValue();
        IModel<T> duplicate = findDuplicate(value);
        if (!Objects.equals(duplicate.getObject(), form.getModelObject()) && duplicate.getObject() != null && form.getModelObject().isNew()) {
            FeedbackMessage message = new FeedbackMessageComponentOnDuplicateEntity<T>(formComponent,
                    String.format("В базе уже есть объект %s, нажмите чтобы загрузить", duplicate), duplicate) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    form.setModelObject(this.getModel().getObject());
                    onUpdate(target);
                }
            };
            form.getFeedbackMessages().add(message);
            validatable.error(new ValidationError("Для предупреждения дубликатов сохранение не возможно"));
        }
    }

    public abstract IModel<T> findDuplicate(S value);

    protected abstract void onUpdate(AjaxRequestTarget target);
}
