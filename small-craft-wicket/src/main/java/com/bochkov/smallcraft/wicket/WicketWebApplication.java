package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.giffing.wicket.spring.boot.starter.app.WicketBootStandardWebApplication;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class WicketWebApplication extends WicketBootStandardWebApplication {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    LegalPersonRepository legalPerson;

    @Autowired
    BoatRepository boatRepository;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();

        locator.set(Person.class, new IConverter<Person>() {
            @Override
            public Person convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(value)
                        .map(str -> locator.getConverter(Long.class).convertToObject(str, Session.get().getLocale()))
                        .flatMap(id -> personRepository.findById(id)).orElse(null);
            }

            @Override
            public String convertToString(Person value, Locale locale) {
                return Optional.ofNullable(value)
                        .map(Person::getId)
                        .map(pk -> locator.getConverter(Long.class).convertToString(pk, Session.get().getLocale()))
                        .orElse(null);
            }
        });
        locator.set(LegalPerson.class, new IConverter<LegalPerson>() {
            @Override
            public LegalPerson convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(value)
                        .map(str -> locator.getConverter(Long.class).convertToObject(str, Session.get().getLocale()))
                        .flatMap(id -> legalPerson.findById(id)).orElse(null);
            }

            @Override
            public String convertToString(LegalPerson value, Locale locale) {
                return Optional.ofNullable(value)
                        .map(LegalPerson::getId)
                        .map(pk -> locator.getConverter(Long.class).convertToString(pk, Session.get().getLocale()))
                        .orElse(null);
            }
        });

        locator.set(Boat.class, new IConverter<Boat>() {
            @Override
            public Boat convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(value)
                        .map(str -> locator.get(Integer.class).convertToObject(str, Session.get().getLocale()))
                        .flatMap(id -> boatRepository.findById(id))
                        .orElse(null);
            }

            @Override
            public String convertToString(Boat value, Locale locale) {
                return Optional.ofNullable(value)
                        .map(Boat::getId)
                        .orElse(null);
            }
        });
        return locator;
    }
}
