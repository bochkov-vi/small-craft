package com.bochkov.smallcraft.wicket.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .map(a -> {
                    String user = null;
                    if (a.getPrincipal() instanceof String) {
                        user = (String) a.getPrincipal();
                    } else if (a.getPrincipal() instanceof UserDetails) {
                        user = ((UserDetails) a.getPrincipal()).getUsername();
                    }
                    return user;
                });
    }
}
