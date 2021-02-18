package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.data.jpa.mask.MaskableProperty;
import com.bochkov.smallcraft.jpa.entity.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SuggestionTest {

    @Autowired
    NotificationRepository repository;

    @Test
    public void test1() {
        MaskableProperty property = new MaskableProperty("captain.phones");
        MaskableProperty property1 = new MaskableProperty("captain.lastName");
        String like = "TECT";
        List<Notification> list =  repository.findAll(MaskableProperty.maskSpecification(like,"number", "captain.lastName", "boat.tailNumber", "boat.registrationNumber","captain.phones"));
        System.out.println(list);
    }
}
