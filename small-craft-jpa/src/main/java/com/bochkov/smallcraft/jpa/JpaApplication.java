package com.bochkov.smallcraft.jpa;

import com.bochkov.smallcraft.jpa.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories
@SpringBootApplication
@EnableJpaAuditing
public class JpaApplication implements CommandLineRunner {

    @Inject
    DataSource dataSource;

    @Inject
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
        System.exit(1);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(dataSource);

    }
}
