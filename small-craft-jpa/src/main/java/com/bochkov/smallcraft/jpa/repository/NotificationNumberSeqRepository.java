package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.NotificationNumberSeq;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface NotificationNumberSeqRepository extends JpaRepository<NotificationNumberSeq, Integer> {

    @Transactional
    default Integer nextValue(Integer year) {
        NotificationNumberSeq seq = findById(year).map(sq -> saveAndFlush(sq.increment())).orElseGet(() -> saveAndFlush(new NotificationNumberSeq(year, 100)));
        return seq.getNumber();
    }
}
