package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

class NotificationSafeSaveRepositoryImpl implements NotificationSafeSaveRepository {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatRepository boatRepository;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Override
    public Notification preapreSave(Notification entity) {
        Optional<Notification> e = Optional.ofNullable(entity);
        e.map(Notification::getCaptain).ifPresent(
                captain -> entity.setCaptain(personRepository.save(captain)));
        e.map(Notification::getBoat).ifPresent(
                boat -> entity.setBoat(boatRepository.save(boat)));
        return entity;
    }
}
