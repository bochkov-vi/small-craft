package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.JpaApplication;
import com.giffing.wicket.spring.boot.starter.web.config.WicketWebInitializerAutoConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JpaApplication.class)
public class WicketSpringBootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(WicketSpringBootApplication.class)
                .run(args);
    }
}
