package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.NotificationNumberSeq;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;

public interface NotificationNumberSeqRepository extends JpaRepository<NotificationNumberSeq, Integer> {



    @Transactional
    default Integer nextValue(Integer year) {
        NotificationNumberSeq seq = findById(year).map(sq -> saveAndFlush(sq.increment())).orElseGet(() -> saveAndFlush(new NotificationNumberSeq(year, 100)));
        return seq.getNumber();
    }

    @Transactional
    default Integer nextValue() {
        return nextValue(LocalDate.now().getYear());
    }

    @Transactional
    default Integer currentValue() {
        return currentValue(LocalDate.now().getYear());
    }

    @Transactional
    default Integer currentValue(Integer year) {
        return findById(year).map(NotificationNumberSeq::getNumber).orElse(null);
    }
}
