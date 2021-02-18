package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.entity.*;
import com.bochkov.smallcraft.jpa.repository.*;
import com.bochkov.smallcraft.wicket.component.Html5AttributesBehavior;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.bochkov.smallcraft.wicket.web.pages.boat.BoatFilterPanel;
import com.giffing.wicket.spring.boot.starter.app.WicketBootSecuredWebApplication;
import com.google.common.base.Strings;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.protocol.https.Scheme;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestablePage;
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

    @Autowired
    IConverterLocator converterLocator;

    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(new Html5AttributesBehavior.InstantiationListener());
       // setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig(8080,8443)));
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        return converterLocator;
    }


    @Override
    public Session newSession(Request request, Response response) {
        SmallCraftWebSession session = (SmallCraftWebSession) super.newSession(request, response);
        session.updateSignIn();
        return session;

    }


}
