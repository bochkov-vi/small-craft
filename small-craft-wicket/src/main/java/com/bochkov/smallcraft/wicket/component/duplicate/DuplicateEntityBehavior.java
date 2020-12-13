package com.bochkov.smallcraft.wicket.component.duplicate;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class DuplicateEntityBehavior<T, E> extends AbstractDefaultAjaxBehavior implements IRequestListener, IValidator<T> {

    IModel<E> entityModel;

    Class<E> entityClass;

    FormComponent<T> formComponent;

    public DuplicateEntityBehavior(IModel<E> entityModel, Class<E> entityClass) {
        this.entityModel = entityModel;
        this.entityClass = entityClass;
    }

    @Override
    protected void onBind() {
        super.onBind();
        formComponent = (FormComponent<T>) getComponent();
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        onRequest(target);
    }

    public void onRequest(AjaxRequestTarget target) {
        target.add(formComponent);
        IRequestParameters pageParameters = RequestCycle.get().getRequest().getRequestParameters();
        Optional.ofNullable(pageParameters.getParameterValue("e").toString()).map(param -> formComponent.getConverter(entityClass).convertToObject(param, formComponent.getLocale())).ifPresent(
                entity -> resolveDuplicate(target, entity)
        );
    }

    public abstract void resolveDuplicate(AjaxRequestTarget target, E entity);


    @Override
    public void validate(IValidatable<T> validatable) {
        findDuplicates(validatable.getValue()).stream().filter(boat -> !Objects.equals(boat, entityModel.getObject())).forEach(
                duplicate -> validatable.error(newError(duplicate))
        );
    }

    public abstract List<E> findDuplicates(T search);


    public ValidationError newError(E duplicate) {
        ValidationError error = new ValidationError(newMessage(duplicate));
        return error;
    }

    public String newMessage(E duplicate) {
        return String.format("В базе найден похожий объект \"%s\" чтобы загрузить нажмите %s", duplicate, createAjaxLink(duplicate));
    }


    public CharSequence createAjaxLink(E entity) {
        CharSequence htmlLink = String.format("<a href=\"#\"><span class=\"fa fa-pencil\" onclick=\"%s\"></span></a>", createCallbackAjaxFunction(entity));
        //htmlLink = Strings.escapeMarkup(htmlLink);
        return htmlLink;
    }

    public CharSequence createCallbackAjaxFunction(E entity) {
        CharSequence url = getCallbackUrl(entity);
        CharSequence func = Strings.escapeMarkup(String.format("Wicket.Ajax.get({\"u\":\"%s\"})", url));
        return func;
    }

    private CharSequence getCallbackUrl(E entity) {
        PageParameters parameters = pageParameters(entity);
        return formComponent.urlForListener(this, parameters);
    }

    public PageParameters pageParameters(E entity) {
        PageParameters parameters = new PageParameters(formComponent.getPage().getPageParameters());
        parameters.add("e", formComponent.getConverter(entityClass).convertToString(entity, formComponent.getLocale()));
        return parameters;
    }


    @Override
    public void detach(Component component) {
        super.detach(component);
        entityModel.detach();
    }

}
