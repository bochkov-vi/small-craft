package com.bochkov.smallcraft.wicket.component.datetimefield;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeField;
import org.apache.wicket.model.IModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeInput extends LocalDateTimeField {

    String datePattern;

    public LocalDateTimeInput(String id) {
        super(id);
    }

    public LocalDateTimeInput(String id, IModel<LocalDateTime> model) {
        super(id, model);
    }

    public LocalDateTimeInput(String id, String datePattern) {
        super(id);
        this.datePattern = datePattern;
    }

    public LocalDateTimeInput(String id, IModel<LocalDateTime> model, String datePattern) {
        super(id, model);
        this.datePattern = datePattern;
    }

    @Override
    protected LocalDateTextField newDateField(String id, IModel<LocalDate> dateFieldModel) {
        return new com.bochkov.wicket.component.LocalDateTextField(id, dateFieldModel, "dd.MM.yyyy");
    }
}
