package com.bochkov.smallcraft.jpa.service;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.repository.ExitNotificationRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExitNotificationCustomSaveServiceImpl implements ExitNotificationCustomSaveService {

    @Autowired
    ExitNotificationRepository repository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatService boatService;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public ExitNotification save(ExitNotification entity) {
        Optional<ExitNotification> e = Optional.ofNullable(entity);
        e.map(ExitNotification::getCaptain).ifPresent(
                captain -> entity.setCaptain(personRepository.save(captain)));
        e.map(ExitNotification::getBoat).ifPresent(
                boat -> entity.setBoat(boatService.save(boat)));
        e.map(ExitNotification::getNotification).ifPresent(
                n -> entity.setNotification(notificationService.save(n)));
        return repository.save(entity);
    }
}
