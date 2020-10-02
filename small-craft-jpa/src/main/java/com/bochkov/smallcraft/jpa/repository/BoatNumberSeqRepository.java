package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.BoatNumberSeq;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface BoatNumberSeqRepository extends JpaRepository<BoatNumberSeq, Integer> {

    @Transactional
    default Integer nextValue(Integer year) {
        BoatNumberSeq seq = findById(year).map(sq -> saveAndFlush(sq.increment())).orElseGet(() -> saveAndFlush(new BoatNumberSeq(year, 100)));
        return seq.getNumber();
    }
}
