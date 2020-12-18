package com.bochkov.smallcraft.wicket.web.pages.notification;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.wicket.web.pages.person.FormComponentInputPanel;
import org.apache.wicket.model.IModel;

public class CaptainPanel extends FormComponentInputPanel {

    public CaptainPanel(String id, IModel<Person> model) {
        super(id, model);
    }

    public CaptainPanel(String id) {
        super(id);
    }
}
