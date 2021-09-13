package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
class BoatSafeSaveRepositoryImpl implements BoatSafeSaveRepository {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BoatNumberSeqRepository boatNumberSeqRepository;

    @Autowired
    LegalPersonRepository legalPersonRepository;


    @Autowired
    UnitRepository unitRepository;

    @Transactional
    @Override
    public Boat prepareSave(Boat entity) {
        Person person = entity.getPerson();
        if (person != null) {
            person = personRepository.save(person);
            entity.setPerson(person);
//            entity.setOwn(person);
        }

        Optional.of(entity).map(Boat::getUnit).filter(Persistable::isNew).ifPresent(unit -> {
            entity.setUnit(unitRepository.safeSave(unit));
        });

        LegalPerson legalPerson = entity.getLegalPerson();
        if (legalPerson != null) {
            legalPerson = legalPersonRepository.save(legalPerson);
            entity.setLegalPerson(legalPerson);
        }


        if (!entity.isNotRegistable()) {
            if (entity.getRegistrationNumber() == null || entity.getRegistrationNumber() < 0) {
                entity.setRegistrationNumber(boatNumberSeqRepository.nextValue());
            }
            if (entity.getRegistrationDate() == null) {
                entity.setRegistrationDate(LocalDate.now());
            }
        } else {
            entity.setRegistrationDate(null);
            entity.setRegistrationNumber(null);
        }
        return entity;

    }

    @PostConstruct
    public void postConstruct() {
        setSequenceValueToMaxOfBoatRegistrationNumber();
    }

    @Transactional
    @Override
    public void setSequenceValueToMaxOfBoatRegistrationNumber() {
        boatNumberSeqRepository.setValue(Math.max(boatNumberSeqRepository.getMaxBoatRegistrationNumber().orElse(1), boatNumberSeqRepository.findTop().getNumber()));
    }

}