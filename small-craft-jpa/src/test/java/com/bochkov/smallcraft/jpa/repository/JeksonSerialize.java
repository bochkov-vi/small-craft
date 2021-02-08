package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Unit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JeksonSerialize {

    @Autowired
    UnitRepository repository;

    @Autowired
    NotificationRepository notificationRepository;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void unitSerialize() throws JsonProcessingException {
        Unit unit = repository.findById(1L).get();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(unit);
        System.out.println(jsonResult);
    }

    @Test
    public void notificationSerialize() throws JsonProcessingException {
        List<Notification> result = notificationRepository.findByNumber(102);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);
        System.out.println(jsonResult);
    }
}
