package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.spring.security.SecureWebSession;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.inject.Inject;
import java.util.Objects;

public class WicketSecuredWebSession extends SecureWebSession {

    @Inject
    AccountRepository accountRepository;



    public WicketSecuredWebSession(Request request) {
        super(request);
    }

    public static WicketSecuredWebSession get() {
        return (WicketSecuredWebSession) Session.get();
    }

    public void updateSignIn() {
        if (!isSessionInvalidated()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            signIn(auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(role -> !Objects.equals(role.getAuthority(), "ROLE_ANONYMOUS")));
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
}
