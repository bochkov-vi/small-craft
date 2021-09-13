package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;


public interface BoatRepository extends JpaRepository<Boat, Long>, JpaSpecificationExecutor<Boat>, BoatSafeSaveRepository {

    List<Boat> findByTailNumber(String tailNumber);

    @Query(nativeQuery = true, value = "SELECT DISTINCT pier FROM(SELECT pier FROM public.boat n WHERE pier ILIKE :mask" +
            " ORDER BY position(:sort in n.pier),length(n.pier),n.pier) as np",
            countQuery = "SELECT count(distinct pier) FROM boat n WHERE pier ILIKE :mask AND :sort IS NOT NULL")
    Page<String> findPierByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findPierByMask(@Param("mask") String mask, Pageable pg) {
        return findPierByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    @Query(nativeQuery = true, value = "SELECT DISTINCT type FROM(SELECT type FROM public.boat n WHERE type ILIKE :mask" +
            " ORDER BY position(:sort in n.type),length(n.type),n.type) as np",
            countQuery = "SELECT count(distinct type) FROM boat n WHERE type ILIKE :mask AND :sort IS NOT NULL")
    Page<String> findTypeByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findTypeByMask(@Param("mask") String mask, Pageable pg) {
        return findTypeByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    Optional<Boat> findTopByOrderByRegistrationNumberDesc();

    /* @Transactional
     default Integer doInitSeq() {
         Integer number = findTopByOrderByRegistrationNumberDesc().map(Boat::getRegistrationNumber).orElse(1001);
         initSequence(number);
         return number;
     }*/
    @Transactional
    default Boat safeSave(Boat entity) {
        return save(prepareSave(entity));
    }

    default Long registeredCount(Specification<Boat> boatAdditionlSpecification, LocalDate localDate) {
        Optional<LocalDate> date = Optional.ofNullable(localDate);
        return count(Specification.where(boatAdditionlSpecification).and((Specification<Boat>) (r, q, b) -> b.and(
                b.lessThanOrEqualTo(r.get("registrationDate"), date.orElseGet(LocalDate::now)),
                b.or(b.greaterThan(r.get("expirationDate"), date.orElseGet(LocalDate::now)), r.get("expirationDate").isNull()))));
    }

    default Long registeredCount(Specification<Boat> boatAdditionlSpecification, Integer year) {
        Optional<LocalDate> date = Optional.of(year).map(y -> LocalDate.now().withYear(y));
        return count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("registrationDate"), date.map(d -> d.with(TemporalAdjusters.lastDayOfYear())).get()),
                b.greaterThanOrEqualTo(r.get("registrationDate"), date.map(d -> d.with(TemporalAdjusters.firstDayOfYear())).get()))));

    }

    default Long unregisteredCount(Specification<Boat> boatAdditionlSpecification, LocalDate date) {
        return count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.lessThanOrEqualTo(r.get("expirationDate"), date)));
    }

    default Long unregisteredCount(Specification<Boat> boatAdditionlSpecification, Integer year) {
        Optional<LocalDate> date = Optional.of(year).map(y -> LocalDate.now().withYear(y));
        return count(Specification.where(boatAdditionlSpecification).and((r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("expirationDate"), date.map(d -> d.with(TemporalAdjusters.lastDayOfYear())).get()),
                b.greaterThanOrEqualTo(r.get("expirationDate"), date.map(d -> d.with(TemporalAdjusters.firstDayOfYear())).get()))));
    }

    List<Boat> findBoatsByPerson(Person p);
}
