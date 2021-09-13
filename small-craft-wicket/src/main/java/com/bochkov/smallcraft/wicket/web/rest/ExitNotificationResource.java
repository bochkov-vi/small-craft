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
import org.wicketstuff.restutils.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ResourcePath("/rest/exitnotification")
public class ExitNotificationResource extends AbstractRestResource<JsonWebSerialDeserial> {

    @SpringBean
    ExitNotificationRepository exitNotificationRepository;

    @SpringBean
    NotificationRepository notificationRepository;


    public ExitNotificationResource() {
        super(new JsonWebSerialDeserial(new JacksonObjectSerialDeserial(new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))));
    }

//    public ExitNotification


    @MethodMapping(value = "/create/notification/{notification}", httpMethod = HttpMethod.GET)
    public ExitNotification createExitNotification(Integer notificationNumber) {
        LocalDate today = LocalDate.now(SmallCraftWebSession.get().getZoneId());
        Notification notification = notificationRepository.findByNumberAndDate(notificationNumber, today).stream().findFirst().orElse(null);
        ExitNotification exit = null;
        if (notification != null) {
            exit = new ExitNotification().putData(notification);
            exit.setExitCallDateTime(LocalDateTime.now());
            exit.setExitDateTime(LocalDateTime.now());
            LocalDateTime eta = today.atStartOfDay().plusDays(1).minusMinutes(1);
            exit.setEstimatedReturnDateTime(eta);
            exit = exitNotificationRepository.safeSave(exit);
        }
        return exit;
    }

    @MethodMapping(value = "/find/notification/{notification}", httpMethod = HttpMethod.GET)
    public List<ExitNotification> findExitNotification(Integer notificationNumber) {
        LocalDateTime today = LocalDate.now(SmallCraftWebSession.get().getZoneId()).atTime(LocalTime.now(SmallCraftWebSession.get().getZoneId()));
        List<ExitNotification> result = exitNotificationRepository.findByNotificationNumber(notificationNumber, today);
        return result;
    }
}
