package com.bochkov.smallcraft.jpa;

import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.bochkov.smallcraft.jpa.repository")
@SpringBootApplication
@EnableJpaAuditing
public class JpaApplication implements CommandLineRunner {

    @Autowired
    BoatRepository dataSource;


    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
        System.exit(1);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(dataSource);

    }
}
