package com.bochkov.smallcraft.wicket.page.legalPerson;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.wicket.page.person.EditPage;
import com.bochkov.smallcraft.wicket.page.person.SelectPerson;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Optional;

public class InputPanel extends GenericPanel<LegalPerson> {

    Form<Person> form = new Form<>("form");

    Component inn = new TextField<>("inn").setOutputMarkupId(true);

    FormComponent<Person> person = new SelectPerson("person").setRequired(true);

    public InputPanel(String id, IModel<LegalPerson> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.setModel(new CompoundPropertyModel(getModel()));
        form.add(new TextField<>("name", String.class).setRequired(true));
        form.add(new TextArea<>("address").setRequired(true));
        form.add(new TextField<>("id").setEnabled(false));
        form.add(person);
        form.add(new AjaxLink<Void>("btn-add-person") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new EditPage() {
                    @Override
                    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
                        super.onAfterSave(target, model);
                        InputPanel.this.getModelObject().setPerson(model.getObject());
                    }
                }.setBackPage(getPage()));
            }
        });
        form.add(inn);

    }

    @Override
    protected void onConfigure() {
        person.setVisible(getModel().map(p -> !p.isNew()).orElse(false).getObject());
        super.onConfigure();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("jquery.inputmask/current/jquery.inputmask.bundle.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
            }
        }));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('9{10,12}')", inn.getMarkupId())));

    }
}
