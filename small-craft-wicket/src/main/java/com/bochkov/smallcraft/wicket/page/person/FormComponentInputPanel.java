package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.Html5AttributesBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.OnChangeDuplicateBehavior;
import com.bochkov.smallcraft.wicket.page.person.component.Select2FirstName;
import com.bochkov.smallcraft.wicket.page.person.component.Select2MiddleName;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.data.model.PersistableModel;
import com.google.common.collect.Lists;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Accessors(chain = true)

public class FormComponentInputPanel extends FormComponentPanel<Person> {


    @Inject
    PersonRepository personRepository;

    @Getter
    @Setter
    boolean canSelect = false;

    FormComponent<String> phone = (FormComponent<String>) new TextField<String>("phone", Model.of(), String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"tel"};
        }
    }.setRequired(true);

    FormComponent<String> email = new TextField<String>("email", Model.of(), String.class) {
        @Override
        protected String[] getInputTypes() {
            return new String[]{"email"};
        }
    }.setRequired(false);

    FormComponent<String> passportSerial = (FormComponent<String>) new TextField<>("passport.serial", Model.of()).setRequired(true).setOutputMarkupId(true);

    FormComponent<String> passportNumber = (FormComponent<String>) new TextField<>("passport.number", Model.of()).setRequired(true).setOutputMarkupId(true);

    FormComponent<String> lastName = new TextField<>("lastName", Model.of(), String.class).setRequired(true);

    FormComponent<String> firstName = new Select2FirstName("firstName", Model.of()).setRequired(true);

    FormComponent<String> middleName = new Select2MiddleName("middleName", Model.of()).setRequired(true);

    FormComponent<LocalDate> passportDate = new LocalDateTextField("passport.date", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<String> passportData = new TextField<String>("passport.data", Model.of()).setRequired(true);

    FormComponent<String> address = new TextArea<String>("address", Model.of()).setRequired(true);

    IModel<Person> selected = PersistableModel.of(id -> {
        return personRepository.findById(id);
    });

    FormComponent<Person> selectPerson = new SelectPerson("person", selected);

    FormComponent<Person> id = new HiddenField<Person>("id", selected, Person.class);

    // com.bochkov.smallcraft.wicket.page.legalPerson.FormComponentInputPanel legalPersonPanel = new com.bochkov.smallcraft.wicket.page.legalPerson.FormComponentInputPanel("legal-person-panel", legalPerson.getModel());


    public FormComponentInputPanel(String id, IModel<Person> model) {
        super(id, model);
    }

    public FormComponentInputPanel(String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {
        List<FeedbackMessage> messages = collectErrorMessages();
        if (messages.isEmpty()) {
            Person person = getModel().getObject();
            //if (!Objects.equals(person, selectPerson.getModelObject())) {
            selectPerson.setModelObject(person);
            id.setModelObject(person);
            phone.setModelObject(getModel().map(Person::getPhone).getObject());
            email.setModelObject(getModel().map(Person::getEmail).getObject());
            firstName.setModelObject(getModel().map(Person::getFirstName).getObject());
            middleName.setModelObject(getModel().map(Person::getMiddleName).getObject());
            lastName.setModelObject(getModel().map(Person::getLastName).getObject());
            passportSerial.setModelObject(getModel().map(Person::getPassport).map(Passport::getSerial).getObject());
            passportNumber.setModelObject(getModel().map(Person::getPassport).map(Passport::getNumber).getObject());
            passportDate.setModelObject(getModel().map(Person::getPassport).map(Passport::getDate).getObject());
            passportData.setModelObject(getModel().map(Person::getPassport).map(Passport::getData).getObject());
            address.setModelObject(getModel().map(Person::getAddress).getObject());
        } else {
            visitChildren(FormComponent.class, (IVisitor<FormComponent, Object>) (cmp, visit) -> {
                if (cmp.getConvertedInput() != null) {
                    cmp.convertInput();
                    cmp.updateModel();
                }
            });
        }
        super.onBeforeRender();
    }

    public List<FeedbackMessage> collectErrorMessages() {
        List<FeedbackMessage> result = Lists.newLinkedList();
        visitChildren(FormComponent.class, new IVisitor<FormComponent, Object>() {
            @Override
            public void component(FormComponent cmp, IVisit<Object> visit) {
                result.addAll(cmp.getFeedbackMessages().messages((IFeedbackMessageFilter) FeedbackMessage::isError));
            }
        });
        return result;
    }


    @Override
    public void convertInput() {
        Person person = null;
        if (canSelect) {
            person = Optional.ofNullable(selectPerson.getConvertedInput()).orElse(new Person());
        } else {
            person = Optional.ofNullable(id.getConvertedInput()).orElse(new Person());
        }
        person.setPhone(phone.getConvertedInput());
        person.setAddress(address.getConvertedInput());
        person.setEmail(email.getConvertedInput());
        person.setFirstName(firstName.getConvertedInput());
        person.setMiddleName(middleName.getConvertedInput());
        person.setLastName(lastName.getConvertedInput());
        Passport passport = person.getPassport();
        if (passport == null) {
            passport = new Passport();
            person.setPassport(passport);
        }
        passport.setData(passportData.getConvertedInput());
        passport.setDate(passportDate.getConvertedInput());
        passport.setNumber(passportNumber.getConvertedInput());
        passport.setSerial(passportSerial.getConvertedInput());
        setConvertedInput(person);
    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        phone.setOutputMarkupId(true);
        add(selectPerson, id);
        add(firstName);
        add(middleName);


        add(lastName);
        add(passportSerial);
        add(passportNumber);
        add(passportDate);
        add(passportData);
        add(phone);
        add(email);
        add(address);

        lastName.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findAll(firstName.getModelObject(), middleName.getModelObject(), lastName.getConvertedInput());
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });
        firstName.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findAll(firstName.getConvertedInput(), middleName.getModelObject(), lastName.getModelObject());
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });
        middleName.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findAll(firstName.getModelObject(), middleName.getConvertedInput(), lastName.getModelObject());
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });

        //add(new Label("select-label", new ResourceModel("person")));
        FormComponentInputPanel.this.streamChildren().filter(component -> component instanceof FormComponent).forEach(cmp -> cmp.setOutputMarkupId(true));
        selectPerson.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setModelObject(selectPerson.getModelObject());
                target.add(FormComponentInputPanel.this);
                FormComponentInputPanel.this.onUpdate(target);
            }
        });

        passportSerial.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findByPassportSerialAndPassportNumber(passportSerial.getConvertedInput(), passportNumber.getModelObject());
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });

        passportNumber.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findByPassportSerialAndPassportNumber(passportSerial.getModelObject(), passportNumber.getConvertedInput());
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });
        phone.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return personRepository.findByPhone(search);
            }
        });
        Html5AttributesBehavior.append(this);
        FormComponentErrorBehavior.append(this);
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
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('9999')", passportSerial.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('999999')", passportNumber.getMarkupId())));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (canSelect) {
            selectPerson.setVisible(true);
            id.setVisible(false);
        } else {
            selectPerson.setVisible(false);
            id.setVisible(true);
        }
    }

    IModel<Person> model(Person p) {
        return PersistableModel.of(p, id -> personRepository.findById(id));
    }

    protected void onUpdate(AjaxRequestTarget target) {
    }
}
