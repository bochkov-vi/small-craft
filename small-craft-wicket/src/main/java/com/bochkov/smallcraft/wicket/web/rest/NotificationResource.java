package com.bochkov.smallcraft.wicket.web.rest;

import com.bochkov.smallcraft.jpa.entity.Notification;
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
import java.util.List;

@ResourcePath("/rest/notification")
public class NotificationResource extends AbstractRestResource<JsonWebSerialDeserial> {

    @SpringBean
    NotificationRepository repository;

    public NotificationResource() {
        super(new JsonWebSerialDeserial(new JacksonObjectSerialDeserial(new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))));
    }

    @MethodMapping(value = "/{id}", httpMethod = HttpMethod.GET)
    public Notification getNotification(Long id) {
        return repository.findById(id).orElse(null);
    }

    @MethodMapping(value = "/number/{id}", httpMethod = HttpMethod.GET)
    public List<Notification> getNotificationByNumber(Integer id) {
        return repository.findByNumberAndDate(id, LocalDate.now(SmallCraftWebSession.get().getZoneId()));
    }
}
