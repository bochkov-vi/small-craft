package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationRepositoryTest extends BoatRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    public static Notification newNnotification() {
        return new Notification().setActivity("test activity")
                .setBoat(newBoat())
                .setCaptain(newPerson())
                .setDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))
                .setDateFrom(firstDateOfYear())
                .setDateTo(lastDateOfYear())
                .setRegions(Sets.newHashSet("region1", "region2"))
                .setTck(false)
                .setTimeOfDay("light time of day")
                .setUnit(new Unit("test unit 2"));
    }

    @Test
    public void findByMask() {
        List list = notificationRepository.findAll(MaskableProperty.maskSpecification("test", "boat.tailNumber", "captain.lastName", "boat.registrationNumber", "boat.model"));
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void createAndSaveNotification() {

    }


    @Test
    public void findPierByMask() {
        notificationRepository.findRegionByMask("Авач", PageRequest.of(1, 10));
    }

    @Test
    public void findTopByBoat() {
        Boat boat = boatRepository.getOne(29L);
        Optional<Notification> n = notificationRepository.findTopByBoatOrderByNumberDesc(boat);
        Assert.assertNotNull(n.orElse(null));
    }

    @Test
    public void findReginByMask() {
        notificationRepository.findRegionByMask("f", PageRequest.of(0, 1));
    }
}
