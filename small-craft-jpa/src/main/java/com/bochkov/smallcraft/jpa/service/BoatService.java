package com.bochkov.smallcraft.jpa.service;

import com.bochkov.smallcraft.jpa.entity.Boat;

import javax.transaction.Transactional;

public interface BoatService {

    Boat save(Boat entity);
}
