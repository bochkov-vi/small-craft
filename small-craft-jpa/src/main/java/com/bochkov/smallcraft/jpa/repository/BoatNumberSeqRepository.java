package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.BoatNumberSeq;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface BoatNumberSeqRepository extends JpaRepository<BoatNumberSeq, Integer> {

    Optional<BoatNumberSeq> findTopByIdOrderById(Integer id);

    default BoatNumberSeq findTop() {
        return findById(0).orElseGet(() -> saveAndFlush(new BoatNumberSeq(0, 1000)));
    }

    @Transactional
    default Integer nextValue() {
        BoatNumberSeq seq = findTop();
        Integer number = seq.nextValue();
        saveAndFlush(seq);
        return number;
    }

    @Transactional
    default BoatNumberSeq setValue(Integer number) {
        BoatNumberSeq seq = findTop();
        seq.setNumber(number);
        saveAndFlush(seq);
        return seq;
    }
}
