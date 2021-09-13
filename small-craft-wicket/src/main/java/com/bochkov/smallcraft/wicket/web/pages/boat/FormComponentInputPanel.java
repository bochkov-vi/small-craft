package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.OnChangeDuplicateBehavior;
import com.bochkov.smallcraft.wicket.web.crud.CompositeInputPanel;
import com.bochkov.smallcraft.wicket.web.pages.legalPerson.FormComponentInput;
import com.bochkov.smallcraft.wicket.web.pages.unit.SessionSelectUnit;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.jpa.model.PersistableModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Accessors(chain = true)
public class FormComponentInputPanel extends CompositeInputPanel<Boat> {

    @Inject
    BoatRepository boatRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    LegalPersonRepository legalPersonRepository;

    @Inject
    BoatNumberSeqRepository boatNumberSeqRepository;

    @Inject
    UnitRepository unitRepository;

    @Getter
    @Setter
    boolean canSelect = false;

    @Getter
    @Setter
    SerializableBiConsumer<IModel<Person>, AjaxRequestTarget> onPersonEdit;

    IModel<Boolean> legalPersonExists = Model.of(false);

    IModel<Boat> selected = PersistableModel.of(id -> boatRepository.findById(id));


    com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel person = new com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel("person",
            PersistableModel.of(id -> personRepository.findById(id))).setCanSelect(true);

    FormComponent<Boat> id = new HiddenField<>("id", selected, Boat.class);

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    FormComponent<Boat> selectBoat = new SelectBoat("boat", selected);

    FormComponent<String> tailNumber = new TextField<>("tailNumber", Model.of());

    FormComponent<Unit> unit = new SessionSelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true).setRequired(true);

    FormComponent<String> type = new SelectType("type", Model.of()).setRequired(true);

    FormComponent<String> serialNumber = new TextField<>("serialNumber", Model.of(), String.class);

    FormComponent<Integer> buildYear = new NumberTextField<>("buildYear", Model.of(), Integer.class);

    FormComponent<BigDecimal> power = new NumberTextField<>("power", Model.of(), BigDecimal.class).setStep(BigDecimal.valueOf(0.1));

    FormComponent<String> model = new TextField<>("model", Model.of(), String.class).setRequired(true);

    FormComponent<LocalDate> registrationDate = new LocalDateTextField("registrationDate", Model.of(), getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> expirationDate = new LocalDateTextField("expirationDate", Model.of(), getString("dateFormat"));

    FormComponent<Integer> registrationNumber = new TextField<>("registrationNumber", Model.of(), Integer.class);

    FormComponent<LegalPerson> legalPerson = new FormComponentInput("legalPerson", PersistableModel.of(id -> legalPersonRepository.findById(id))).setCanSelect(true);

    FormComponent<Boolean> notRegistable = new CheckBox("notRegistable", Model.of(false));

    WebMarkupContainer registrationPanel = new WebMarkupContainer("registrationPanel");

    public FormComponentInputPanel(String id, IModel<Boat> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        person.setOnEdit(onPersonEdit);
        super.onInitialize();
        setOutputMarkupId(true);

        //============REGISTRATION INPUTS
        add(registrationPanel);
        registrationPanel.setOutputMarkupId(true);
        WebMarkupContainer registrationContent = new WebMarkupContainer("registrationContent") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!notRegistable.getModelObject());
            }
        };
        registrationContent.setOutputMarkupId(true);
        queue(unit, power);
        pier.setRequired(true);
        registrationContent.add(registrationDate, registrationNumber, expirationDate, buildYear, serialNumber, pier);
        registrationPanel.add(registrationContent);
        add(id, selectBoat, tailNumber, model, type);
        add(notRegistable);
        registrationNumber.setOutputMarkupId(true);
        registrationContent.add(new AjaxLink<Void>("generateNewNumber") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(registrationNumber);
                registrationNumber.setModelObject(boatNumberSeqRepository.nextValue());
            }
        });

        tailNumber.add(new OnChangeDuplicateBehavior<String, Boat>(getModel(), Boat.class) {
            @Override
            public void resolveDuplicate(AjaxRequestTarget target, Boat entity) {
                setModelObject(entity);
                target.add(FormComponentInputPanel.this);
            }

            @Override
            public List<Boat> findDuplicates(String search) {
                return boatRepository.findByTailNumber(search);
            }

            @Override
            public IModel<Boat> newModel(Boat entity) {
                return PersistableModel.of(entity, id -> boatRepository.findById(id));
            }
        });

       /* tailNumber.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                boatRepository.findByTailNumber(validatable.getValue()).stream().filter(duplicate -> !Objects.equals(duplicate, tailNumber.getConvertedInput())).findFirst().ifPresent(
                        duplicate -> {
                            PageParameters parameters = new PageParameters().set("boat", getConverter(Boat.class).convertToString(duplicate, getSession().getLocale()));
                            CharSequence url = urlForListener(loadBoatListener, parameters);
                            String message = String.format("Найдет дупликат <i class='fa fa-pencil' onclick='Wicket.Ajax.get({\\'u\\':\\'%s\\'})'/>", url);
                            validatable.error(new ValidationError(message));
                        }
                );
            }
        });*/
        tailNumber.setOutputMarkupId(true);

        //==========================================================================
        //==========================================================================
        notRegistable.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(registrationPanel);
                if (getModelObject() != null) {
                    getModelObject().setNotRegistable(notRegistable.getModelObject());
                }
            }
        });
        //===========================================================================


        FormComponentErrorBehavior.append(this);
        add(person);
        setOutputMarkupId(true);
        selectBoat.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(FormComponentInputPanel.this);
                setModelObject(selectBoat.getModelObject());
                FormComponentInputPanel.this.onUpdate(target);
            }
        });
        WebMarkupContainer legalPersonPanel = new WebMarkupContainer("legal-person-panel") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                legalPerson.setEnabled(legalPersonExists.getObject());
                legalPerson.setVisible(legalPersonExists.getObject());
            }
        };
        legalPersonPanel.setOutputMarkupId(true);
        add(legalPersonPanel);
        AbstractLink btnAddLegal = new AjaxLink<Void>("btn-add-legal-person") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                legalPersonExists.setObject(!legalPersonExists.getObject());
                target.add(legalPersonPanel);
            }
        };
        btnAddLegal.add(new Label("btn-add-legal-person-label", legalPersonExists.map(val -> getString("enableLegalPerson." + !val))));
        legalPersonPanel.add(btnAddLegal, legalPerson);

    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (canSelect) {
            selectBoat.setVisible(true);
            id.setVisible(false);
            selectBoat.setEnabled(true);
            id.setEnabled(false);
        } else {
            selectBoat.setVisible(false);
            id.setVisible(true);
            selectBoat.setEnabled(false);
            id.setEnabled(true);
        }


//        if (!notRegistable.getModelObject()) {
//            registrationNumber.setRequired(true);
//            registrationDate.setRequired(true);
//        } else {
//            registrationNumber.setRequired(false);
//            registrationDate.setRequired(false);
//        }

        legalPersonExists.setObject(getModel().map(Boat::getLegalPerson).filter(Objects::nonNull).map(lp -> true).orElse(false).getObject());

    }

    @Override
    public void convertInput() {
        Boat boat = null;
        if (canSelect) {
            boat = selectBoat.getConvertedInput();
        } else {
            boat = id.getConvertedInput();
        }
        if (boat == null) {
            boat = new Boat();
        }
        boat.setLegalPerson(legalPerson.getConvertedInput());
        boat.setPerson(person.getConvertedInput());
        boat.setTailNumber(tailNumber.getConvertedInput());
        boat.setModel(model.getConvertedInput());
        boat.setType(type.getConvertedInput());
        boat.setRegistrationDate(registrationDate.getConvertedInput());
        boat.setExpirationDate(expirationDate.getConvertedInput());
        boat.setRegistrationNumber(registrationNumber.getConvertedInput());
        boat.setUnit(unit.getConvertedInput());
        boat.setSerialNumber(serialNumber.getConvertedInput());
        boat.setBuildYear(buildYear.getConvertedInput());
        boat.setNotRegistable(notRegistable.getConvertedInput());
        boat.setPower(power.getConvertedInput());
        boat.setPier(pier.getConvertedInput());
        setConvertedInput(boat);
    }

    @Override
    protected void initBeforeRenderer() {

        selected.setObject(getModel().getObject());
        tailNumber.setModelObject(getModel().map(Boat::getTailNumber).getObject());
        serialNumber.setModelObject(getModel().map(Boat::getSerialNumber).getObject());
        buildYear.setModelObject(getModel().map(Boat::getBuildYear).getObject());
        unit.setModelObject(getModel().map(Boat::getUnit).orElseGet(() -> unit.getModelObject()).getObject());
        type.setModelObject(getModel().map(Boat::getType).getObject());
        model.setModelObject(getModel().map(Boat::getModel).getObject());
        registrationDate.setModelObject(getModel().map(Boat::getRegistrationDate).getObject());
        expirationDate.setModelObject(getModel().map(Boat::getExpirationDate).getObject());
        registrationNumber.setModelObject(getModel().map(Boat::getRegistrationNumber).getObject());
        person.setModelObject(getModel().map(Boat::getPerson).getObject());
        legalPerson.setModelObject(getModel().map(Boat::getLegalPerson).getObject());
        notRegistable.setModelObject(getModel().map(Boat::isNotRegistable).orElseGet(() -> notRegistable.getModelObject()).getObject());
        power.setModelObject(getModel().map(Boat::getPower).orElse(null).getObject());
        pier.setModelObject(getModel().map(Boat::getPier).orElse(null).getObject());

    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    public void onUpdate(AjaxRequestTarget target) {
    }
}
