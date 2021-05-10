package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel;
import com.bochkov.wicket.jpa.model.PersistableModel;
import com.google.common.collect.Lists;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CaptainPanel extends FormComponentPanel<Person> {

    IModel<Person> personModel;

    @Inject
    PersonRepository personRepository;

    FormComponentInputPanel captain = new FormComponentInputPanel("captain", PersistableModel.of(id -> personRepository.findById(id))) {
        @Override
        protected void initBeforeRenderer() {
            super.initBeforeRenderer();
            configureVisible();
        }

        @Override
        public List<Person> onDuplicatePhoneFinded(List<Person> personListWithEqPhone) {
            return CaptainPanel.this.onDuplicatePhoneFinded(personListWithEqPhone);
        }
    };


    public CaptainPanel(String id, IModel<Person> captainModel, IModel<Person> personModel) {
        super(id, captainModel);
        this.personModel = personModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer("content");
        container.setOutputMarkupId(true);
        container.add(captain);
        add(container);
        captain.setCanSelect(true);
        container.add(new AjaxLink<Boolean>("btn-captain-eq-owner") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (isCaptainEqOwner()) {
                    captain.setModelObject(null);
                } else {
                    captain.setModelObject(personModel.getObject());
                }
                configureVisible();
                target.add(container);
            }
        }.add(new Label("btn-captain-eq-owner-label", new StringResourceModel("btn-captain-eq-owner.${captainEqOwner}", Model.of(this)).setParameters(isCaptainEqOwner()))));

    }

    @Override
    protected void onBeforeRender() {
        captain.setModelObject(getModelObject());
        super.onBeforeRender();
    }

    @Override
    public void convertInput() {
        Person captainPerson = captain.getConvertedInput();
        if (captainPerson == null) {
            captainPerson = personModel.getObject();
        }
        setConvertedInput(captainPerson);
        //captain.setCanSelect(true);
    }

    public boolean isCaptainEqOwner() {
        Person captain = this.captain.getModelObject();
        Person person = this.personModel.getObject();
        return captain!=null && Objects.equals(captain, person);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

    }

    public void configureVisible() {
        if (isCaptainEqOwner()) {
            captain.setEnabled(false);
            captain.setVisible(false);
        } else {
            captain.setEnabled(true);
            captain.setVisible(true);
        }
    }

    public List<Person> onDuplicatePhoneFinded(List<Person> personListWithEqPhone) {
        return personListWithEqPhone;
    }
}
