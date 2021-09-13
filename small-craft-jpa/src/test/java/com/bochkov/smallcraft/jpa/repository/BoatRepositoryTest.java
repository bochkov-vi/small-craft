package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Unit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BoatRepositoryTest extends PersonRepositoryTest {

    @Autowired
    BoatRepository boatRepository;


    @Autowired
    UnitRepository unitRepository;


    public static Boat newBoat() {
        Boat boat = new Boat();
        boat.setPerson(newPerson()).setModel("test model").setType("МЛ").setRegistrationDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))
                .setTailNumber("-----").setUnit(new Unit().setName("test unit"));
        return boat;
    }

    @Test
    public void newEntity() {
        Boat boat = newBoat();
        boat = boatRepository.safeSave(boat);
        boatRepository.delete(boat);
    }

    @Test
    public void findPierByMask() {
        boatRepository.findPierByMask("Фреза", PageRequest.of(1, 10));
    }

   /* @Test
    public void doInitSeq(){
        Integer number = repository.doInitSeq();
        Assert.assertNotNull(number);
        Assert.assertTrue(number>0);
    }*/
}
