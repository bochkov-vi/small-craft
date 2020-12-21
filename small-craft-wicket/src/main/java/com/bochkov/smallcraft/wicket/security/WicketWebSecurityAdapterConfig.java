package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.giffing.wicket.spring.boot.context.security.AuthenticatedWebSessionConfig;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * Default Spring Boot Wicket security getting started configuration. Its only
 * active if there is not other {@link WebSecurityConfigurerAdapter} present.
 * <p>
 * Holds hard coded users which should only be used to get started
 *
 * @author Marc Giffing
 */
@Configuration
public class WicketWebSecurityAdapterConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    AccountRepository accountRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.anonymous().disable()
                .csrf().disable()

//                .authorizeRequests().antMatchers("/boat/**","/person/**","/notification/**","/exit-notification/**","/unit/**").hasAnyRole("USER", "ADMIN")
//                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .logout()
                .permitAll()
                .and().rememberMe()
        ;
        http.headers().frameOptions().disable();
    }

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
                User.withUsername("admin")
                        .password(passwordEncoder().encode("admin"))
                        .authorities("USER", "ADMIN")
                        .build());
        return manager;
    }

    @Bean
    public AuthenticatedWebSessionConfig authenticatedWebSessionConfig() {
        return new AuthenticatedWebSessionConfig() {

            @Override
            public Class<? extends AbstractAuthenticatedWebSession> getAuthenticatedWebSessionClass() {
                return WicketSecuredWebSession.class;
            }
        };
    }

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        RememberMeServices services = new TokenBasedRememberMeServices("rmkey",userDetailsService);
        return services;
    }


}
