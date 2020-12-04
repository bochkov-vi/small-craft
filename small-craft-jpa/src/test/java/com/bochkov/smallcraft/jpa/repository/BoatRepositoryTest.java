package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Passport;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.service.BoatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BoatRepositoryTest {

    @Autowired
    BoatRepository repository;

    @Autowired
    BoatService service;

    @Autowired
    UnitRepository unitRepository;

    @Test
    public void newEntity() {
        Boat boat = new Boat();
        boat.setUnit(unitRepository.getOne(1L));
        boat.setRegistrationNumber(0);
        boat.setPier("test pier");
        boat.setType("test type");
        boat.setModel("test boat model");
        boat.setPerson(new Person().setAddress("test person address").setFirstName("test name")
                .setLastName("test last name")
                .setMiddleName("test middle name")
                .setPassport(new Passport().setData("test passport data")
                        .setDate(LocalDate.now()).setNumber("test passport numer")
                        .setSerial("test"))
                .setPhone("test phone"));
        service.save(boat);
    }

    @Test
    public void findPierByMask() {
        repository.findPierByMask("Фреза", PageRequest.of(1, 10));
    }

   /* @Test
    public void doInitSeq(){
        Integer number = repository.doInitSeq();
        Assert.assertNotNull(number);
        Assert.assertTrue(number>0);
    }*/
}