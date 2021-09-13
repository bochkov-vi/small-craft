package com.bochkov.smallcraft.wicket.web.pages.boat;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.repository.BoatNumberSeqRepository;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.wicket.jpa.model.PersistableModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.util.Optional;

@MountPath("boat/edit")
public class EditPage extends CrudEditPage<Boat, Long> {

    Component inputPanel;

    @SpringBean
    BoatRepository repository;

    @SpringBean
    PersonRepository personRepository;

    @SpringBean
    LegalPersonRepository legalPersonRepository;

    @SpringBean
    BoatNumberSeqRepository boatNumberSeqRepository;


    public EditPage(PageParameters parameters) {
        super(Boat.class, parameters);
    }

    public EditPage(IModel<Boat> model) {
        super(Boat.class, model);
    }


    public EditPage() {
        super(Boat.class);
    }

    @Override
    public Boat save(Boat entity) {
        return repository.safeSave(entity);
    }

    @Override
    protected Component createInputPanel(String id, IModel<Boat> model) {
        inputPanel = new InputPanel(id, model) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedback);
            }

        };
        inputPanel.setOutputMarkupId(true);
        return inputPanel;
    }

    @Override
    public BoatRepository getRepository() {
        return repository;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        feedback.setEscapeModelStrings(false);
    }

    @Override
    public Boat newEntityInstance() {
        return super.newEntityInstance().setBuildYear(LocalDate.now().getYear());
    }

    @Override
    public AbstractLink createCloneButton(String id, IModel<Boat> model) {
        {
            AbstractLink link = super.createCloneButton(id, model);
            link.setEnabled(true).setVisible(true);
            return link;
        }
    }

    @Override
    public void onClone(Optional<AjaxRequestTarget> target, IModel<Boat> model) {

        setResponsePage(new EditPage(PersistableModel.of(id -> repository.findById(id), () -> {
            Boat boat = new Boat();
            boat.setPerson(model.map(Boat::getPerson).getObject());
            boat.setRegistrationDate(LocalDate.now());
            boat.setLegalPerson(model.map(Boat::getLegalPerson).getObject());
            boat.setUnit(model.map(Boat::getUnit).getObject());
            return boat;
        })));
    }
}