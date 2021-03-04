package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.spring.security.SecureWebSession;
import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.TimeZone;

public class SmallCraftWebSession extends SecureWebSession {

    @Inject
    AccountRepository accountRepository;


    public SmallCraftWebSession(Request request) {
        super(request);
    }

    public static SmallCraftWebSession get() {
        return (SmallCraftWebSession) Session.get();
    }

    public ZoneId getZoneId() {
        return getTimezone().toZoneId();
    }

    public TimeZone getTimezone() {
        TimeZone timezone = null;
        if (timezone == null) {
            timezone = getClientInfo().getProperties().getTimeZone();
        }
        if (timezone == null) {
            timezone = TimeZone.getDefault();
        }
        return timezone;
    }

    public void updateSignIn() {
        if (!isSessionInvalidated()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            signIn(auth != null && auth.isAuthenticated());
        }
    }

    public Account getCurrentAccount() {
        if (isSignedIn()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.isAuthenticated()) {
                return accountRepository.findById(auth.getName()).orElse(null);
            }
        }
        return null;
    }

    @Override
    public void signOut() {
        super.signOut();
        SecurityContextHolder.clearContext();
    }

    public static class SmallCraftWebSessionListener implements ISessionListener {

        @Override
        public void onCreated(Session session) {
            if (session instanceof SmallCraftWebSession) {
                ((SmallCraftWebSession) session).updateSignIn();
            }
        }
    }
}
