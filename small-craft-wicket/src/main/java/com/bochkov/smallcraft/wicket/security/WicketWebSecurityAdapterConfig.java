package com.bochkov.smallcraft.wicket.security;

import com.bochkov.smallcraft.jpa.entity.Account;
import com.bochkov.smallcraft.jpa.repository.AccountRepository;
import com.giffing.wicket.spring.boot.context.security.AuthenticatedWebSessionConfig;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Default Spring Boot Wicket security getting started configuration. Its only
 * active if there is not other {@link WebSecurityConfigurerAdapter} present.
 * <p>
 * Holds hard coded users which should only be used to get started
 *
 * @author Marc Giffing
 */
@Configuration
@Order(5)
@EnableWebSecurity
public class WicketWebSecurityAdapterConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountRepository accountRepository;

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userSearchFilter("(sAMAccountName={0})")
                .userDetailsContextMapper(new UserDetailsContextMapper() {
                    @Override
                    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String s, Collection<? extends GrantedAuthority> collection) {
                        return userDetailsService.loadUserByUsername(s);
                    }

                    @Override
                    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {

                    }
                })
                .contextSource()
                .url("ldap://main.svpubo.fsb.ru:389/dc=9862,dc=svpubo,dc=fsb,dc=ru");
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                Account account = accountRepository.findById(authentication.getName()).orElse(null);
                if (account != null) {
                    if (Objects.equals(account.getPassword(), authentication.getCredentials())) {
                        authentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
                                account.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                    } else {
                        authentication = null;
                    }
                }
                return authentication;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                boolean result = UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
                return result;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .logout()
                .permitAll()
                .and().rememberMe().rememberMeServices(rememberMeServices()).userDetailsService(userDetailsService)
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .permitAll();
        ;
        http.headers().frameOptions().disable();
    }

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Bean
    public AuthenticatedWebSessionConfig authenticatedWebSessionConfig() {
        return new AuthenticatedWebSessionConfig() {

            @Override
            public Class<? extends AbstractAuthenticatedWebSession> getAuthenticatedWebSessionClass() {
                return SmallCraftWebSession.class;
            }
        };
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        RememberMeServices services = new PersistentTokenBasedRememberMeServices("rmkey", userDetailsService, persistentTokenRepository);
        return services;
    }


}
