package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.BoatNumberSeq;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;


public class BoatNumberSequenceRepositoryImpl implements BoatNumberSequenceRepository {


    @Autowired
    BoatNumberSeqRepository boatNumberSeqRepository;


    @Transactional
    public Integer generateNextValue() {
        return boatNumberSeqRepository.nextValue();
    }

    @Transactional
    public BoatNumberSeq initSequence(Integer number) {
        return boatNumberSeqRepository.setValue(number);
    }
}
