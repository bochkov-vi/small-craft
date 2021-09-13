package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import org.springframework.transaction.annotation.Transactional;

interface BoatSafeSaveRepository {

    @Transactional
    Boat prepareSave(Boat entity);

    @Transactional
    void setSequenceValueToMaxOfBoatRegistrationNumber();
}
