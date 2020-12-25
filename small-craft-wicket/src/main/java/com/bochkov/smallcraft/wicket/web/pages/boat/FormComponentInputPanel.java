package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.jpa.repository.UnitRepository;
import com.bochkov.smallcraft.wicket.component.FormComponentErrorBehavior;
import com.bochkov.smallcraft.wicket.component.Html5AttributesBehavior;
import com.bochkov.smallcraft.wicket.component.duplicate.OnChangeDuplicateBehavior;
import com.bochkov.smallcraft.wicket.web.pages.legalPerson.FormComponentInput;
import com.bochkov.smallcraft.wicket.web.pages.unit.SelectUnit;
import com.bochkov.wicket.component.LocalDateTextField;
import com.bochkov.wicket.data.model.PersistableModel;
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

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Accessors(chain = true)
public class FormComponentInputPanel extends FormComponentPanel<Boat> {

    @Inject
    BoatRepository boatRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    LegalPersonRepository legalPersonRepository;

    @Inject
    UnitRepository unitRepository;

    @Getter
    @Setter
    boolean canSelect = false;


    IModel<Boolean> legalPersonExists = Model.of(false);

    IModel<Boat> selected = PersistableModel.of(id -> boatRepository.findById(id));


    FormComponent<Person> person = new com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel("person",
            PersistableModel.of(id -> personRepository.findById(id))).setCanSelect(true);

    FormComponent<Boat> id = new HiddenField<>("id", selected, Boat.class);

    FormComponent<Boat> selectBoat = new SelectBoat("boat", selected);

    FormComponent<String> tailNumber = new TextField<>("tailNumber", Model.of());

    FormComponent<Unit> unit = new SelectUnit("unit", PersistableModel.of(id -> unitRepository.findById(id))).setRequired(true);

    FormComponent<String> type = new SelectType("type", Model.of());

    FormComponent<String> serialNumber = new TextField<>("serialNumber", Model.of(), String.class);

    FormComponent<Integer> buildYear = new NumberTextField<>("buildYear", Model.of(), Integer.class);

    FormComponent<String> pier = new SelectPier("pier", Model.of());

    FormComponent<String> model = new TextField<>("model", Model.of());

    FormComponent<LocalDate> registrationDate = new LocalDateTextField("registrationDate", Model.of(), getString("dateFormat"));

    FormComponent<LocalDate> expirationDate = new LocalDateTextField("expirationDate", Model.of(), getString("dateFormat"));

    FormComponent<Integer> registrationNumber = new TextField<>("registrationNumber", Model.of(), Integer.class);

    FormComponent<LegalPerson> legalPerson = new FormComponentInput("legalPerson", PersistableModel.of(id -> legalPersonRepository.findById(id))).setCanSelect(true);


    public FormComponentInputPanel(String id, IModel<Boat> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);

        add(id, selectBoat, tailNumber, model, type, pier, unit, buildYear, serialNumber);
        add(registrationDate, registrationNumber, expirationDate);


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
        Html5AttributesBehavior.append(this);
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
        boat.setPier(pier.getConvertedInput());
        boat.setRegistrationDate(registrationDate.getConvertedInput());
        boat.setExpirationDate(expirationDate.getConvertedInput());
        boat.setRegistrationNumber(registrationNumber.getConvertedInput());
        boat.setUnit(unit.getConvertedInput());
        boat.setSerialNumber(serialNumber.getConvertedInput());
        boat.setBuildYear(buildYear.getConvertedInput());
        setConvertedInput(boat);
    }

    @Override
    protected void onBeforeRender() {
        Optional<Boat> boat = Optional.ofNullable(getModelObject());
        selected.setObject(boat.orElse(null));
        tailNumber.setModelObject(boat.map(Boat::getTailNumber).orElse(null));
        serialNumber.setModelObject(boat.map(Boat::getSerialNumber).orElse(null));
        buildYear.setModelObject(boat.map(Boat::getBuildYear).orElse(null));
        unit.setModelObject(boat.map(Boat::getUnit).orElse(null));
        type.setModelObject(boat.map(Boat::getType).orElse(null));
        pier.setModelObject(boat.map(Boat::getPier).orElse(null));
        model.setModelObject(boat.map(Boat::getModel).orElse(null));
        registrationDate.setModelObject(boat.map(Boat::getRegistrationDate).orElse(null));
        expirationDate.setModelObject(boat.map(Boat::getExpirationDate).orElse(null));
        registrationNumber.setModelObject(boat.map(Boat::getRegistrationNumber).orElse(null));
        person.setModelObject(boat.map(Boat::getPerson).orElse(null));
        legalPerson.setModelObject(boat.map(Boat::getLegalPerson).orElse(null));
        super.onBeforeRender();

    }

    @Override
    public IModelComparator getModelComparator() {
        return IModelComparator.ALWAYS_FALSE;
    }

    public void onUpdate(AjaxRequestTarget target) {
    }
}
