package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.JpaApplication;
import com.bochkov.smallcraft.wicket.security.SpringSecurityAuditorAware;
import com.giffing.wicket.spring.boot.context.security.AuthenticatedWebSessionConfig;
import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.spring.security.SecureWebSession;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Optional;

@SpringBootApplication
@Import(JpaApplication.class)
public class WicketSpringBootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(WicketSpringBootApplication.class)
                .run(args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WicketSpringBootApplication.class);
    }

    @Bean
    AuditorAware<String> auditorAware() {
        AuditorAware auditorAware = new SpringSecurityAuditorAware();
        return auditorAware;
    }
}
