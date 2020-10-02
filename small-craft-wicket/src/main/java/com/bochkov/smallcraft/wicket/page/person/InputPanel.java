package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.wicket.component.LocalDateTextField;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

public class InputPanel extends GenericPanel<Person> {

    Form<Person> form = new Form<>("form");

    @Inject
    PersonRepository personRepository;

    Component phone = new TextField<String>("phone", String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"tel"};
        }
    }.setRequired(true).setOutputMarkupId(true);

    Component email = new TextField<String>("email", String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"email"};
        }
    }.setRequired(false);

    public InputPanel(String id, IModel<Person> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.setModel(new CompoundPropertyModel(getModel()));
        form.add(new AutoCompleteTextField<String>("firstName", String.class) {
            @Override
            protected Iterator<String> getChoices(String pattern) {
                return personRepository.findFirstNameByMask(pattern).iterator();
            }
        }.setRequired(true));
        form.add(new AutoCompleteTextField<String>("middleName", String.class) {
            @Override
            protected Iterator<String> getChoices(String input) {
                return personRepository.findMiddleNameByMask(input).iterator();
            }
        }.setRequired(true));
        form.add(new TextField<>("lastName", String.class).setRequired(true));
        form.add(new TextField<>("passport.serial", Integer.class).setRequired(true));
        form.add(new TextField<>("passport.number", Integer.class).setRequired(true));
        form.add(new LocalDateTextField("passport.date", getString("dateFormat")).setRequired(true));
        form.add(new TextField<>("passport.data").setRequired(true));
        form.add(phone);
        form.add(email);
        form.add(new TextArea<>("address").setRequired(true));
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
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('+7(999) 999-99-99')", phone.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('email')", email.getMarkupId())));

    }
}
