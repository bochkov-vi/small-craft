package com.bochkov.smallcraft.wicket.web.model;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import com.bochkov.wicket.jpa.model.JacksonModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JsonModel {

    @Autowired
    NotificationRepository repository;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void notificationMode() {
        Notification notification = repository.findByNumber(0).stream().findFirst().get();
        JacksonModel<Notification> model = new JacksonModel(notification);
        model.detach();
        System.out.println(model.getKey());
        Notification notification1 = model.getObject();
        notification1.setCreateDate(LocalDateTime.now());
        Person person = notification1.getCaptain();
        person.getPhones().add("93-23");
        notification1.setCaptain(personRepository.save(person));
        notification1.setNumber(0);
        notification1 = repository.save(notification1);
        System.out.println(notification1);


    }

}
