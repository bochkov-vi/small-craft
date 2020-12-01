package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;


public interface BoatRepository extends JpaRepository<Boat, Long>, JpaSpecificationExecutor<Boat>, BoatNumberSequenceRepository {

    Boat findByTailNumber(String tailNumber);

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
    default <S extends Boat> S safeSave(S entity) {
        if (entity != null && (entity.getRegistrationNumber() == null || entity.getRegistrationNumber() <= 0)) {
            entity.setRegistrationNumber(generateNextValue());
        }
        return save(entity);
    }
}
