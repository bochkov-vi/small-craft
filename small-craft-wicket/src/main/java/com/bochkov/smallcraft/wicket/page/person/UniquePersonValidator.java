package com.bochkov.smallcraft.wicket.page.person;

import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Objects;

public abstract class UniquePersonValidator extends AbstractFormValidator {

    FormComponent<String> serial;

    FormComponent<String> number;

    public UniquePersonValidator(FormComponent<String> serial, FormComponent<String> number) {
        this.serial = serial;
        this.number = number;
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return new FormComponent[]{serial, number};
    }

    @Override
    public void validate(Form<?> form) {
        Person person = (Person) form.getModelObject();
        Person finded = getJpaRepository().findByPassportSerialAndPassportNumber(serial.getInput(), number.getInput()).orElse(null);
        if (finded!=null && !Objects.equals(finded, person)) {
            CharSequence url = RequestCycle.get().urlFor(EditPage.class, new PageParameters().set(0, finded.getId()));
            serial.error("Физическое лицо с такой серией паспорта уже есть в базе <a href='" + url + "'>перейти к редактированию</a>");
            number.error("Физическое лицо с таким номером паспорта уже есть в базе <a href='" + url + "'>перейти к редактированию</a>");

        }
    }

    protected abstract PersonRepository getJpaRepository();
}
