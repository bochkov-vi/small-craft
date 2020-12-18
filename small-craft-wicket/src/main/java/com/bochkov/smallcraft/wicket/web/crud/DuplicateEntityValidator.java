package com.bochkov.smallcraft.wicket.web.crud;

import com.bochkov.smallcraft.wicket.web.crud.duplicate.FeedbackMessageComponentOnDuplicateEntity;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Persistable;

import java.util.List;

public abstract class DuplicateEntityValidator<T extends Persistable, S> implements IValidator<S> {

    FormComponent<S> formComponent;

    Form<T> form;

    SerializableFunction<T, IModel<T>> modelSupplier;

    public DuplicateEntityValidator(FormComponent<S> formComponent, SerializableFunction<T, IModel<T>> modelSupplier) {
        this.formComponent = formComponent;
        this.form = (Form<T>) formComponent.getForm();
        this.modelSupplier = modelSupplier;
    }

    @Override
    public void validate(IValidatable<S> validatable) {
        S value = validatable.getValue();
        List<T> duplicates = findDuplicates(value);
        T entity = form.getModelObject();

        for (T duplicate : duplicates) {

            FeedbackMessage message = new FeedbackMessageComponentOnDuplicateEntity<T>(formComponent,
                    String.format("В базе уже есть объект %s, нажмите чтобы загрузить", duplicate), modelSupplier.apply(duplicate)) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    form.setModelObject(this.getModel().getObject());
                    onUpdate(target,duplicate);
                }
            };
            formComponent.getFeedbackMessages().add(message);
            validatable.error(new ValidationError("Для предупреждения дубликатов сохранение не возможно"));
       }
    }

    public abstract List<T> findDuplicates(S value);

    protected abstract void onUpdate(AjaxRequestTarget target,T duplicate);
}
