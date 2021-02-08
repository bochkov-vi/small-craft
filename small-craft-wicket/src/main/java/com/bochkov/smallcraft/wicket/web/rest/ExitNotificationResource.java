package com.bochkov.smallcraft.wicket.web.rest;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.wicket.security.SmallCraftWebSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.JacksonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ResourcePath("/rest/exitnotification")
public class ExitNotificationResource extends AbstractRestResource<JsonWebSerialDeserial> {

    @SpringBean
    ExitNotificationRepository repository;

    @SpringBean
    NotificationRepository notificationRepository;

    public ExitNotificationResource() {
        super(new JsonWebSerialDeserial(new JacksonObjectSerialDeserial(new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))));
    }

    public ExitNotification


    @MethodMapping("/exit/{notification}")
    public ExitNotification createExit(Integer notNumber) {
        LocalDate today = LocalDate.now(SmallCraftWebSession.get().getZoneId());
        Notification notification = notificationRepository.findByNumberAndDate(notNumber, LocalDate.now(SmallCraftWebSession.get().getZoneId())).stream().findFirst().orElse(null);
        ExitNotification exit = null;
        if (notification != null) {
            exit = new ExitNotification().putData(notification);
            exit.setExitCallDateTime(LocalDateTime.now());
            exit.setExitDateTime(LocalDateTime.now());
            LocalDateTime eta = LocalDate.now(SmallCraftWebSession.get().getZoneId()).atStartOfDay().plusDays(1).minusMinutes(1);
            exit.setEstimatedReturnDateTime(eta);
            exit = repository.safeSave(exit);
        }
        return exit;
    }
}
