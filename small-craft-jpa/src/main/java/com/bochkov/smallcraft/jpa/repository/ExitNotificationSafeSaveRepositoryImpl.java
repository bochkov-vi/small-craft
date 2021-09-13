package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

class ExitNotificationSafeSaveRepositoryImpl implements ExitNotificationSafeSaveRepository {


    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatRepository boatRepository;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Autowired
    NotificationRepository notificationSafeSaveRepository;

    @Override
    public ExitNotification prepareSave(ExitNotification entity) {
        Optional<ExitNotification> e = Optional.ofNullable(entity);
        e.map(ExitNotification::getCaptain).ifPresent(
                captain -> entity.setCaptain(personRepository.save(captain)));
        e.map(ExitNotification::getBoat).ifPresent(
                boat -> entity.setBoat(boatRepository.save(boat)));
        e.map(ExitNotification::getNotification).ifPresent(
                n -> entity.setNotification(notificationSafeSaveRepository.save(n)));

        return entity;
    }
}
