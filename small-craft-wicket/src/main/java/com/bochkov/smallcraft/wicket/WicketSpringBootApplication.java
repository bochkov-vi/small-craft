package com.bochkov.smallcraft.wicket;

import com.bochkov.smallcraft.jpa.JpaApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

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
}
