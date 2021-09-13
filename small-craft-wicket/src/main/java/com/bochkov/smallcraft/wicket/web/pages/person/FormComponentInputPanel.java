package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.OnChangeDuplicateBehavior;
import com.bochkov.smallcraft.wicket.component.phone.PhonesInput;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.person.component.Select2FirstName;
import com.bochkov.smallcraft.wicket.web.pages.person.component.Select2MiddleName;
import com.bochkov.wicket.component.InputMaskBehavior;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.SetModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Accessors(chain = true)

public class FormComponentInputPanel extends CompositeInputPanel<Person> {


    @Inject
    PersonRepository personRepository;

    @Getter
    @Setter
    boolean canSelect = false;

    @Getter
    @Setter
    boolean canEdit = true;

    @Getter
    @Setter
    SerializableBiConsumer<IModel<Person>, AjaxRequestTarget> onEdit;

    PhonesInput phones = (PhonesInput) new PhonesInput("phone", new SetModel<>()).setRequired(true);

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

    FormComponent<LocalDate> birthDate = new LocalDateTextField("birthDate", Model.of(), getString("dateFormat")).setRequired(false);

    FormComponent<String> passportData = new TextField<String>("passport.data", Model.of()).setRequired(true);

    FormComponent<String> address = new TextArea<String>("address", Model.of()).setRequired(true);

    IModel<Person> selected = PersistableModel.of(id -> {
        return personRepository.findById(id);
    });

    FormComponent<Person> selectPerson = new SelectPerson("person", selected);

    FormComponent<Person> id = new HiddenField<Person>("id", selected, Person.class);

    // com.bochkov.smallcraft.wicket.page.store.legalPerson.FormComponentInputPanel legalPersonPanel = new com.bochkov.smallcraft.wicket.page.store.legalPerson.FormComponentInputPanel("legal-person-panel", legalPerson.getModel());


    public FormComponentInputPanel(String id, IModel<Person> model) {
        super(id, model);
    }

    public FormComponentInputPanel(String id) {
        super(id);
    }

    @Override
    protected void initBeforeRenderer() {
        List<FeedbackMessage> messages = collectErrorMessages();
        if (messages.isEmpty()) {
            Person person = getModel().getObject();
            //if (!Objects.equals(person, selectPerson.getModelObject())) {
            selectPerson.setModelObject(person);
            id.setModelObject(person);
            phones.setModelObject(getModel().map(Person::getPhones).map(Sets::newHashSet).orElseGet(Sets::newHashSet).getObject());
            email.setModelObject(getModel().map(Person::getEmail).getObject());
            firstName.setModelObject(getModel().map(Person::getFirstName).getObject());
            middleName.setModelObject(getModel().map(Person::getMiddleName).getObject());
            lastName.setModelObject(getModel().map(Person::getLastName).getObject());
            passportSerial.setModelObject(getModel().map(Person::getPassport).map(Passport::getSerial).getObject());
            passportNumber.setModelObject(getModel().map(Person::getPassport).map(Passport::getNumber).getObject());
            passportDate.setModelObject(getModel().map(Person::getPassport).map(Passport::getDate).getObject());
            passportData.setModelObject(getModel().map(Person::getPassport).map(Passport::getData).getObject());
            address.setModelObject(getModel().map(Person::getAddress).getObject());
            birthDate.setModelObject(getModel().map(Person::getBirthDate).getObject());
        } else {
            visitChildren(FormComponent.class, (IVisitor<FormComponent, Object>) (cmp, visit) -> {
                if (cmp.getConvertedInput() != null) {
                    cmp.convertInput();
                    cmp.updateModel();
                }
            });
        }
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
        person.setPhones(Sets.newHashSet(phones.getConvertedInput()));
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
        person.setBirthDate(birthDate.getConvertedInput());
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
        phones.setOutputMarkupId(true);
        add(selectPerson, id);
        add(firstName);
        add(middleName);

        add(birthDate);
        add(lastName);
        add(passportSerial);
        add(passportNumber);
        add(passportDate);
        add(passportData);
        add(phones);
        add(email);
        add(address);
        WebMarkupContainer editLinkContainer = new WebMarkupContainer("edit-prepend-container") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(onEdit != null);
            }
        };
        editLinkContainer.add(new AjaxLink<Person>("edit", selectPerson.getModel()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit.accept(this.getModel(), target);
            }
        });
        add(editLinkContainer);
        /*lastName.add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
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
        });*/

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
        //passportSerial.add(new InputMaskBehavior("{ regex: `([0-9]{4}|[A-Z]{2})`}"));
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
        passportNumber.add(new InputMaskBehavior("999999"));
        phones.getPhoneInput().add(new OnChangeDuplicateBehavior<String, Person>(getModel(), Person.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Person entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Person> findDuplicates(String search) {
                return onDuplicatePhoneFinded(personRepository.findByPhones(search));
            }

            @Override
            public IModel<Person> newModel(Person entity) {
                return model(entity);
            }
        });

        email.add(InputMaskBehavior.email());
        FormComponentErrorBehavior.append(this);
    }

    public List<Person> onDuplicatePhoneFinded(List<Person> personListWithEqPhone) {
        return personListWithEqPhone;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
/*        response.render(JavaScriptHeaderItem.forReference(new WebjarsJavaScriptResourceReference("jquery.inputmask/current/jquery.inputmask.bundle.js") {
            @Override
            public List<HeaderItem> getDependencies() {
                return Lists.newArrayList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
            }
        }));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('+7(999) 999-99-99')", phone.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('email')", email.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('9999')", passportSerial.getMarkupId())));
        response.render(OnDomReadyHeaderItem.forScript(String.format("$('#%s').inputmask('999999')", passportNumber.getMarkupId())));*/
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

        phones.setEnabled(canEdit);
        email.setEnabled(canEdit);
        passportSerial.setEnabled(canEdit);
        passportNumber.setEnabled(canEdit);
        lastName.setEnabled(canEdit);
        firstName.setEnabled(canEdit);
        middleName.setEnabled(canEdit);
        passportDate.setEnabled(canEdit);
        birthDate.setEnabled(canEdit);
        passportData.setEnabled(canEdit);
        address.setEnabled(canEdit);
    }

    IModel<Person> model(Person p) {
        return PersistableModel.of(p, id -> personRepository.findById(id));
    }

    protected void onUpdate(AjaxRequestTarget target) {
    }
}
