package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.BoatNumberSeq;

public interface BoatNumberSequenceRepository {

    Integer generateNextValue();

    BoatNumberSeq initSequence(Integer number);
}
