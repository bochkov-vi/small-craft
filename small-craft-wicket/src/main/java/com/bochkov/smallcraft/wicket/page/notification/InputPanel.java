package com.bochkov.smallcraft.wicket.page.notification;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.NotificationPK;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.wicket.page.boat.SelectBoat;
import com.bochkov.smallcraft.wicket.page.person.SelectPerson;
import com.bochkov.wicket.component.LocalDateTextField;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.time.LocalDate;

public class InputPanel extends GenericPanel<Notification> {

    Form<T> form = new Form<>("form");

    FormComponent<NotificationPK> id = new PkComponent("id");

    FormComponent<String> region = new SelectRegion("region");

    FormComponent<Person> captain = new SelectPerson("captain");

    FormComponent<Boat> boat = new SelectBoat("boat");

    FormComponent<LocalDate> date = new LocalDateTextField("date", getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateFrom = new LocalDateTextField("dateFrom", getString("dateFormat")).setRequired(true);

    FormComponent<LocalDate> dateTo = new LocalDateTextField("dateTo", getString("dateFormat")).setRequired(true);

    FormComponent<String> activity = new TextField<>("activity");

    FormComponent<String> timeOfDay = new TextField<>("timeOfDay");

    FormComponent<Boolean> tck = new CheckBox("tck");


    public InputPanel(String id, IModel<Notification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form);
        form.setModel(new CompoundPropertyModel(getModel()));
        form.add(id, region, captain, boat, date, dateFrom, dateTo, activity, timeOfDay, tck);
        captain.setOutputMarkupId(true);
        boat.setOutputMarkupId(true);
        boat.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(captain);
                Person own = boat.getModel().map(Boat::getOwn).getObject();
                if (captain.getModelObject() == null && own != null) {
                    captain.setModelObject(own);
                }
            }
        });
        captain.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
    }

}
