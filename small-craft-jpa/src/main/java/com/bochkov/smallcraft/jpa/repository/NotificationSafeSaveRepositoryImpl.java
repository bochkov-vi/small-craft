package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

class NotificationSafeSaveRepositoryImpl implements NotificationSafeSaveRepository {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatRepository boatRepository;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Autowired
    NotificationNumberSeqRepository seqRepository;

    @Override
    @Transactional
    public Notification preapreSave(Notification entity) {
        Optional<Notification> e = Optional.ofNullable(entity);
        e.map(Notification::getBoat).ifPresent(
                boat -> entity.setBoat(boatRepository.safeSave(boat)));
        if(entity.getCaptain()==null){
            entity.setCaptain(e.map(Notification::getBoat).map(Boat::getPerson).orElse(null));
        }
        e.map(Notification::getCaptain).ifPresent(
                captain -> entity.setCaptain(personRepository.save(captain)));

        if (entity != null) {
            if (entity.getNumber() == null) {
                entity.setNumber(seqRepository.nextValue(entity.getYear()));
            }
        }
        {
            boolean pier = e.map(Notification::getPier).map(str -> !Strings.isNullOrEmpty(str)).orElse(false);
            boolean activity = e.map(Notification::getActivities).map(set -> !set.isEmpty()).orElse(false);
            boolean regions = e.map(Notification::getRegions).map(set -> !set.isEmpty()).orElse(false);
            boolean canVoiceCall = e.map(Notification::getCanVoiceCall).orElse(false);
            e.ifPresent(n -> n.setCanVoiceCall(canVoiceCall && pier && activity && regions));
        }
        return entity;
    }
}
