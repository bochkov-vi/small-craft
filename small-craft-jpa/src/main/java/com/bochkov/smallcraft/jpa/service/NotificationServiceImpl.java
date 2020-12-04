package com.bochkov.smallcraft.jpa.service;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.NotificationRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository repository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatService boatService;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Override
    public Notification save(Notification entity) {
        Optional<Notification> e = Optional.ofNullable(entity);
        e.map(Notification::getCaptain).ifPresent(
                captain -> entity.setCaptain(personRepository.save(captain)));
        e.map(Notification::getBoat).ifPresent(
                boat -> entity.setBoat(boatService.save(boat)));
        return repository.save(entity);
    }
}
