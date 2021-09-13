package com.bochkov.smallcraft.jpa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.bochkov.smallcraft.jpa.repository")
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
        System.exit(1);
    }

    @Override
    public void run(String... args) throws Exception {


    }

    @Bean
    AuditorAware<String> auditorAware() {
        AuditorAware auditorAware = new AuditorAware() {
            @Override
            public Optional getCurrentAuditor() {
                return Optional.empty();
            }
        };
        return auditorAware;
    }
}
