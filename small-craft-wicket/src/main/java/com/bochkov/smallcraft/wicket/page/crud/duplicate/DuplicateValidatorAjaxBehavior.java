package com.bochkov.smallcraft.wicket.page.crud.duplicate;

import org.apache.wicket.IRequestListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import java.util.Optional;

public class DuplicateValidatorAjaxBehavior<T> extends AjaxFormComponentUpdatingBehavior implements IValidator<T> {

    IRequestListener actionListener = new IRequestListener() {
        @Override
        public void onRequest() {
            onAjaxAction(RequestCycle.get().find(AjaxRequestTarget.class));
        }

        @Override
        public boolean rendersPage() {
            return false;
        }
    };

    public DuplicateValidatorAjaxBehavior(String event) {
        super(event);
    }


    public DuplicateValidatorAjaxBehavior() {
        super("change");
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {

    }

    @Override
    protected void onError(AjaxRequestTarget target, RuntimeException e) {
        super.onError(target, e);
    }

    @Override
    public void validate(IValidatable<T> validatable) {

    }


    public void onAjaxAction(Optional<AjaxRequestTarget> targetOptional) {

    }

}
