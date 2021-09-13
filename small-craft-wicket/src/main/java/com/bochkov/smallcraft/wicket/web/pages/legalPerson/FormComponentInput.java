package com.bochkov.smallcraft.wicket.web.pages.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import javax.inject.Inject;
import java.util.List;

@Accessors(chain = true)
public class FormComponentInput extends CompositeInputPanel<LegalPerson> {

    @Inject
    LegalPersonRepository repository;

    @Getter
    @Setter
    boolean canSelect = false;

    IModel<LegalPerson> selectedEntity = PersistableModel.of(repository::findById);

    org.apache.wicket.markup.html.form.FormComponent<String> inn = new TextField<>("inn", Model.of(), String.class);

    org.apache.wicket.markup.html.form.FormComponent<String> name = new TextField<>("name", Model.of(), String.class).setRequired(true);

    org.apache.wicket.markup.html.form.FormComponent<String> address = new TextArea<String>("address", Model.of());

    org.apache.wicket.markup.html.form.FormComponent<LegalPerson> select = new SelectLegalPerson("select", selectedEntity);

    FormComponent<LegalPerson> id = new HiddenField<LegalPerson>("id", selectedEntity, LegalPerson.class);

    public FormComponentInput(String id, IModel<LegalPerson> model) {
        super(id, model);
    }

    public FormComponentInput(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        select.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(FormComponentInput.this);
                setModelObject(select.getModelObject());
            }
        });
        add(name);
        add(address);
        add(select, id);
        add(inn);
        FormComponentErrorBehavior.append(this);
    }

    @Override
    protected void initBeforeRenderer() {
        LegalPerson legalPerson = getModelObject();
        select.setModelObject(legalPerson);
        inn.setModelObject(getModel().map(LegalPerson::getInn).getObject());
        inn.add(new PatternValidator("[0-9]{10,12}"));
        name.setModelObject(getModel().map(LegalPerson::getName).getObject());
        address.setModelObject(getModel().map(LegalPerson::getAddress).getObject());
    }

    @Override
    public void convertInput() {
        LegalPerson legalPerson = select.getModelObject();
        if (legalPerson == null) {
            legalPerson = new LegalPerson();
        }
        legalPerson.setAddress(address.getConvertedInput());
        legalPerson.setInn(inn.getConvertedInput());
        legalPerson.setName(name.getConvertedInput());
        setConvertedInput(legalPerson);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (canSelect) {
            select.setVisible(true);
            id.setVisible(false);
            select.setEnabled(true);
            id.setEnabled(false);

        } else {
            select.setVisible(false);
            id.setVisible(true);
            select.setEnabled(false);
            id.setEnabled(true);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (isVisibleInHierarchy() && this.isEnabledInHierarchy()) {
            response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("jquery.inputmask/current/jquery.inputmask.bundle.js") {
                @Override
                public List<HeaderItem> getDependencies() {
                    return Lists.newArrayList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
                }
            }));
            response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('9{10,12}')", inn.getMarkupId())));
        }
    }
}
