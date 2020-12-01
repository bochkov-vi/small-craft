package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationRepositoryTest {

    @Autowired
    NotificationRepository repository;

    @Autowired
    BoatRepository boatRepository;

    @Test
    public void findPierByMask() {
        repository.findRegionByMask("Авач", PageRequest.of(1, 10));
    }

    @Test
    public void findTopByBoat() {
        Boat boat = boatRepository.getOne(29L);
        Optional<Notification> n = repository.findTopByBoatOrderByNumberDesc(boat);
        Assert.assertNotNull(n.orElse(null));
    }

    @Test
    public void findReginByMask(){
        repository.findRegionByMask("f",PageRequest.of(0,1));
    }
}