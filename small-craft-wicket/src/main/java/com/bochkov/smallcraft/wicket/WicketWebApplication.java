package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.Html5AttributesBehavior;
import com.bochkov.smallcraft.wicket.security.WicketSecuredWebSession;
import com.giffing.wicket.spring.boot.starter.app.WicketBootSecuredWebApplication;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class WicketWebApplication extends WicketBootSecuredWebApplication {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    LegalPersonRepository legalPerson;

    @Autowired
    BoatRepository boatRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    ExitNotificationRepository exitNotificationRepository;

    @Autowired
    UnitRepository unitRepository;

    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(new Html5AttributesBehavior.InstantiationListener());
        getRequestCycleListeners().add(new IRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                WicketSecuredWebSession.get().updateSignIn();
            }

            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                return null;
            }
        });
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();


        locator.set(Person.class, new IConverter<Person>() {
            @Override
            public Person convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(value)
                        .map(str -> {
                            Long id = null;
                            try {
                                id = locator.getConverter(Long.class).convertToObject(str, Session.get().getLocale());
                            } catch (ConversionException e) {
                            }
                            return id;
                        })
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
        locator.set(Notification.class, new IConverter<Notification>() {
            @Override
            public Notification convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(locator.getConverter(Long.class).convertToObject(value, Session.get().getLocale())).flatMap(notificationRepository::findById).orElse(null);
            }

            @Override
            public String convertToString(Notification value, Locale locale) {
                return Optional.ofNullable(value).map(Notification::getId).map(id -> locator.getConverter(Long.class).convertToString(id, Session.get().getLocale())).orElse(null);
            }
        });

        locator.set(ExitNotification.class, new IConverter<ExitNotification>() {
            @Override
            public ExitNotification convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(locator.getConverter(Long.class).convertToObject(value, Session.get().getLocale())).flatMap(exitNotificationRepository::findById).orElse(null);
            }

            @Override
            public String convertToString(ExitNotification value, Locale locale) {
                return Optional.ofNullable(value).map(ExitNotification::getId).map(id -> locator.getConverter(Long.class).convertToString(id, Session.get().getLocale())).orElse(null);
            }
        });

        locator.set(Unit.class, new IConverter<Unit>() {
            @Override
            public Unit convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(locator.getConverter(Long.class).convertToObject(value, Session.get().getLocale())).flatMap(unitRepository::findById).orElse(null);
            }

            @Override
            public String convertToString(Unit value, Locale locale) {
                return Optional.ofNullable(value).map(Unit::getId).map(id -> locator.getConverter(Long.class).convertToString(id, Session.get().getLocale())).orElse(null);
            }
        });

        locator.set(Boat.class, new IConverter<Boat>() {
            @Override
            public Boat convertToObject(String value, Locale locale) throws ConversionException {
                return Optional.ofNullable(value)
                        .map(str -> locator.getConverter(Long.class).convertToObject(str, Session.get().getLocale()))
                        .flatMap(id -> boatRepository.findById(id))
                        .orElse(null);
            }

            @Override
            public String convertToString(Boat value, Locale locale) {
                return Optional.ofNullable(value)
                        .map(Boat::getId)
                        .map(pk -> locator.getConverter(Long.class).convertToString(pk, Session.get().getLocale()))
                        .orElse(null);
            }
        });

        return locator;
    }


    @Override
    public Session newSession(Request request, Response response) {
        return super.newSession(request, response);
    }


}
