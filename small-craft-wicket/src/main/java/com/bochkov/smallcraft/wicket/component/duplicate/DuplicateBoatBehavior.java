package com.bochkov.smallcraft.wicket.component.duplicate;

import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.IRequestListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class DuplicateBoatBehavior<T, E> extends FormComponentErrorBehavior implements IRequestListener, IValidator<T> {

    IModel<E> entityModel;

    Class<E> entityClass;

    public DuplicateBoatBehavior(IModel<E> entityModel, Class<E> entityClass) {
        this.entityModel = entityModel;
        this.entityClass = entityClass;
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
    }

    @Override
    public void onRequest() {
        RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(this::onRequest);

    }

    public void onRequest(AjaxRequestTarget target) {
        target.add(formComponent);
        PageParameters pageParameters = target.getPageParameters();
        Optional.ofNullable(pageParameters.get("e").toString()).map(param -> formComponent.getConverter(entityClass).convertToObject(param, formComponent.getLocale())).ifPresent(
                entity -> resolveDuplicate(target, entity)
        );
    }

    public abstract void resolveDuplicate(AjaxRequestTarget target, E entity);


    @Override
    public void validate(IValidatable<T> validatable) {
        findDuplicates(validatable.getValue()).stream().filter(boat -> !Objects.equals(boat, entityModel.getObject())).forEach(
                duplicate -> validatable.error(new ValidationError(
                        String.format("В базе найден похожий объект %s", duplicate)))
        );
    }

    public abstract List<E> findDuplicates(T search);


    public String createJavaScript(FeedbackMessage message, E entity) {
//        String.format("$('#%s').after('<div class=&quot;invalid-feedback&quot;>%s</div>')",
//                formComponent.getMarkupId(), String.valueOf(message.getMessage()));

        CharSequence insertedHtml = String.format("<div class=\"invalid-feedback\"><span>%s</span>%s</a>", message.getMessage(), createAjaxLink(entity));
        //insertedHtml = Strings.escapeMarkup(insertedHtml);
        return String.format("$('#%s').after('%s')", formComponent.getMarkupId(), insertedHtml);
    }

    public CharSequence createAjaxLink(E entity) {
        CharSequence htmlLink = String.format("<a href=\"#\"><span class=\"fa fa-pencil\" onclick=\"%s\"></span></a>", createCallbackAjaxFunction(entity));
        htmlLink = Strings.escapeMarkup(htmlLink);
        return htmlLink;
    }

    public CharSequence createCallbackAjaxFunction(E entity) {
        CharSequence url = getCallbackUrl(entity);
        CharSequence func = Strings.escapeMarkup(String.format("Wicket.Ajax.get({'u':'%s'})", url));
        return func;
    }

    private CharSequence getCallbackUrl(E entity) {
        PageParameters parameters = new PageParameters(formComponent.getPage().getPageParameters());
        parameters.add("e", formComponent.getConverter(entityClass).convertToString(entity, formComponent.getLocale()));
        return formComponent.urlForListener(this, parameters);
    }


    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
    }

    @Override
    public void detach(Component component) {
        super.detach(component);
        entityModel.detach();
    }

}
