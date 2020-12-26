package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    static public Passport newPassport() {
        return new Passport().setData("test passport data").setSerial("0000").setNumber("000000").setDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
    }

    static public Person newPerson() {
        return new Person().setAddress("*").setPassport(newPassport()).setFirstName("Test").setLastName("Test").setMiddleName("Test").setPhone("00000000000");
    }

    public static LocalDate firstDateOfYear() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate lastDateOfYear() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }

    @Test
    public void findAllNames() {
        List<Person> list = personRepository.findAll("А", "А", "Пупкин");
    }

    @Test
    public void testSave() {
        Person person = newPerson();
        person = personRepository.save(person);
        personRepository.delete(person);
    }

    @Test
    public void testFindByMask() {
        Page page = personRepository.findByMask(null, PageRequest.of(0, 10));
        Assert.assertNotNull(page);
    }

    @Test
    public void findBy() {
        List<Person> people = personRepository.findByPhones("+7(000) 000-00-00");
        Assert.assertTrue(people.isEmpty());
    }

    @Test
    public void findFirstName() {
        Page page = personRepository.findFirstNameByMask(null, PageRequest.of(1, 10));
        Assert.assertFalse(page.isEmpty());
    }
}
