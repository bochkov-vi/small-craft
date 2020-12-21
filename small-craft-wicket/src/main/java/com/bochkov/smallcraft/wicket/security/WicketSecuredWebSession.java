package com.bochkov.smallcraft.wicket.security;

import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.spring.security.SecureWebSession;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class WicketSecuredWebSession extends SecureWebSession {
    public WicketSecuredWebSession(Request request) {
        super(request);
    }

    public static WicketSecuredWebSession get() {
        return (WicketSecuredWebSession) Session.get();
    }

    public void updateSignIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        signIn(auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(role -> !Objects.equals(role.getAuthority(), "ROLE_ANONYMOUS")));
    }
}
