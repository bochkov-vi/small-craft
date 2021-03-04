package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.giffing.wicket.spring.boot.starter.app.WicketBootSecuredWebApplication;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WicketWebApplication extends WicketBootSecuredWebApplication {

    @Inject
    IConverterLocator converterLocator;

    @Override
    protected void init() {
        super.init();

        // setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig(8080,8443)));
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        return converterLocator;
    }


   /* @Override
    public Session newSession(Request request, Response response) {
        SmallCraftWebSession session = (SmallCraftWebSession) super.newSession(request, response);
        session.updateSignIn();
        return session;
    }*/


}
