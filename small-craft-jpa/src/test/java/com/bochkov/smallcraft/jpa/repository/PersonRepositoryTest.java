package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    public void testSave() {
        Person person = new Person();
        person.setFirstName("Александр")
                .setMiddleName("Олегович")
                .setLastName("Андриенков")
                .setAddress("г. ПК, Ларина 8/3-28");
        person.setPassport(new Passport()
                .setSerial("3013")
                .setNumber("516049")
                .setDate(LocalDate.of(2020, 5, 14))
                .setData("ТП УФМС России по КК в пос. Ключи")
        );
        personRepository.save(person);
    }

    @Test
    public void testFindByMask() {
        personRepository.findByMask("Анд", PageRequest.of(0,10));

    }
}