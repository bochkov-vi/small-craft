package com.bochkov.smallcraft.jpa.service;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.LegalPerson;
import com.bochkov.smallcraft.jpa.entity.Person;
import com.bochkov.smallcraft.jpa.repository.BoatNumberSeqRepository;
import com.bochkov.smallcraft.jpa.repository.BoatRepository;
import com.bochkov.smallcraft.jpa.repository.LegalPersonRepository;
import com.bochkov.smallcraft.jpa.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BoatServiceImpl implements BoatService {


    @Autowired
    BoatNumberSeqRepository boatNumberSeqRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    LegalPersonRepository legalPersonRepository;

    @Autowired
    BoatRepository boatRepository;

    @Transactional
    @Override
    public Boat save(Boat entity) {
        Person person = entity.getPerson();
        if (person != null) {
            person = personRepository.save(person);
            entity.setPerson(person);
//            entity.setOwn(person);
        }

        LegalPerson legalPerson = entity.getLegalPerson();
        if (legalPerson != null) {
            legalPerson = legalPersonRepository.save(legalPerson);
            entity.setLegalPerson(legalPerson);
        }

        if (entity != null && (entity.getRegistrationNumber() == null || entity.getRegistrationNumber() <= 0)) {
            entity.setRegistrationNumber(boatNumberSeqRepository.nextValue());
        }
        return boatRepository.save(entity);
    }
}
