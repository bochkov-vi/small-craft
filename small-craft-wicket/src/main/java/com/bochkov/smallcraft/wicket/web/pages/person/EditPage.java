package com.bochkov.smallcraft.wicket.web.pages.person;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.crud.CrudEditPage;
import com.bochkov.smallcraft.wicket.web.crud.CrudTablePage;
import com.bochkov.wicket.jpa.model.PersistableModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@MountPath("person/edit")
public class EditPage extends CrudEditPage<Person, Long> {

    @SpringBean
    PersonRepository repository;

    @SpringBean
    LegalPersonRepository legalPersonRepository;

    @SpringBean
    NotificationRepository notificationRepository;

    @SpringBean
    BoatRepository boatRepository;

    public EditPage(PageParameters parameters) {
        super(Person.class, parameters);
    }

    public EditPage(IModel<Person> model) {
        super(Person.class, model);
    }

    public EditPage() {
        super(Person.class);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        feedback.setEscapeModelStrings(false);
        add(new ListView<Boat>("boats", LoadableDetachableModel.of(() -> getModel().filter(p->!p.isNew()).map(p -> boatRepository.findBoatsByPerson(p)).getObject())) {
            @Override
            protected void populateItem(ListItem<Boat> item) {
                item.add(new Label("boat", item.getModel().map(Boat::toString)));
                item.add(CrudTablePage.createEditButton("edit",PersistableModel.of(item.getModelObject(),btpk->boatRepository.findById(btpk)),
                        boatModel->{
                            com.bochkov.smallcraft.wicket.web.pages.boat.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.boat.EditPage();
                            editPage.setModel(boatModel);
                            editPage.setBackPage(getPage());
                            setResponsePage(editPage);
                        },
                        getPage()));
            }
        });
        add(new ListView<Notification>("notifications", LoadableDetachableModel.of(() -> getModel().filter(p->!p.isNew()).map(p -> notificationRepository.findByCaptainOrBoatPerson(p, LocalDate.now(SmallCraftWebSession.get().getZoneId()))).getObject())) {
            @Override
            protected void populateItem(ListItem<Notification> item) {
                item.add(new Label("boat", item.getModel().map(Notification::getBoat).map(Boat::toString)));
                item.add(new Label("notification", item.getModel().map(Notification::getNumber)));
                item.add(new Label("year", item.getModel().map(Notification::getYear)));
                item.add(new Label("dateFrom", item.getModel().map(Notification::getDateFrom).map(d -> d.format(DateTimeFormatter.ofPattern(getString("dateFormat"))))));
                item.add(new Label("dateTo", item.getModel().map(Notification::getDateTo).map(d -> d.format(DateTimeFormatter.ofPattern(getString("dateFormat"))))));
                item.add(CrudTablePage.createEditButton("edit", PersistableModel.of(item.getModelObject(), ntpk -> notificationRepository.findById(ntpk)),
                        model -> {
                            com.bochkov.smallcraft.wicket.web.pages.notification.EditPage editPage = new com.bochkov.smallcraft.wicket.web.pages.notification.EditPage(model);
                            setResponsePage(editPage);
                            editPage.setBackPage(this.getPage());
                        }, EditPage.this.getPage()
                ));
            }
        });
    }

    @Override
    protected Component createInputPanel(String id, IModel<Person> model) {
        return new InputPanel(id, model) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedback);
            }
        };
    }

    @Override
    public PersonRepository getRepository() {
        return repository;
    }

    @Override
    public void onAfterSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onAfterSave(target, model);
    }

    @Override
    public void onSave(Optional<AjaxRequestTarget> target, IModel<Person> model) {
        super.onSave(target, model);
    }


    @Override
    public Person newEntityInstance() {
        return new Person().setPassport(new Passport());
    }

}
