package com.bochkov.smallcraft.wicket.page.store.notification;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.wicket.page.store.person.FormComponentInputPanel;
import org.apache.wicket.model.IModel;

public class CaptainPanel extends FormComponentInputPanel {

    public CaptainPanel(String id, IModel<Person> model) {
        super(id, model);
    }

    public CaptainPanel(String id) {
        super(id);
    }
}
