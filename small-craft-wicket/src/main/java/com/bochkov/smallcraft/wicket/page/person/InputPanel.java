package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.page.legalPerson.EditPage;
import com.bochkov.smallcraft.wicket.page.legalPerson.SelectLegalPerson;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
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

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class InputPanel extends GenericPanel<Person> {

    Form<Person> form = new Form<>("form");

    @Inject
    LegalPersonRepository legalPersonRepository;

    @Inject
    PersonRepository personRepository;

    FormComponent phone = (FormComponent) new TextField<String>("phone", String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"tel"};
        }
    }.setRequired(true).setOutputMarkupId(true);

    FormComponent email = new TextField<String>("email", String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"email"};
        }
    }.setRequired(false);

    FormComponent<String> serial = (FormComponent<String>) new TextField<>("passport.serial", String.class).setRequired(true).setOutputMarkupId(true);

    FormComponent<String> number = (FormComponent<String>) new TextField<>("passport.number", String.class).setRequired(true).setOutputMarkupId(true);

    SelectLegalPerson legalPerson = new SelectLegalPerson("legalPerson");

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
        form.add(serial);
        form.add(number);
        form.add(new LocalDateTextField("passport.date", getString("dateFormat")).setRequired(true));
        form.add(new TextField<>("passport.data").setRequired(true));
        form.add(phone);
        form.add(email);
        form.add(new UniquePersonValidator(serial, number) {
            @Override
            protected PersonRepository getJpaRepository() {
                return personRepository;
            }
        });
        form.add(new TextArea<>("address").setRequired(true));


        form.add(legalPerson);
        form.add(new AjaxLink<Void>("btn-add-legal-person") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new EditPage(new PersistableModel<LegalPerson, Long>() {
                    @Override
                    public Optional<LegalPerson> findById(Long aLong) {
                        Optional<LegalPerson> finded = Optional.ofNullable(aLong).flatMap(id -> legalPersonRepository.findById(id));
                        return finded;
                    }

                    @Override
                    public LegalPerson ifNullGet() {
                        return new LegalPerson().setPerson(InputPanel.this.getModelObject());
                    }
                }) {
                    @Override
                    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<LegalPerson> model) {
                        super.onAfterSave(target, model);
                        InputPanel.this.getModelObject().setLegalPerson(model.getObject());
                    }

                }.setBackPage(getPage()));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(legalPerson.isVisible());
            }
        }.setOutputMarkupPlaceholderTag(true));
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
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('9999')", serial.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('999999')", number.getMarkupId())));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        Person person = getModelObject();
        legalPerson.setVisible(person != null && !person.isNew());
    }
}
