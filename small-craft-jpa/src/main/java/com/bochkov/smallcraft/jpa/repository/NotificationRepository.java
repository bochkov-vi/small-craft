package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification>, NotificationSafeSaveRepository {

    @Query(nativeQuery = true, value = "SELECT distinct region FROM (SELECT region FROM(SELECT n.region FROM notification_region n WHERE region ILIKE :mask UNION DISTINCT SELECT n.region FROM exit_notification_region n WHERE region ILIKE :mask) as t ORDER BY position(:sort in region), length(region), region) as t",
            countQuery = "SELECT count(distinct region) FROM (SELECT n.region FROM notification_region n WHERE region ILIKE :mask UNION DISTINCT SELECT n.region FROM exit_notification_region n WHERE region ILIKE :mask)as t WHERE :sort IS NOT NULL\n")
    Page<String> findRegionByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findRegionByMask(@Param("mask") String mask, Pageable pg) {
        return findRegionByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    Optional<Notification> findTopByBoatOrderByNumberDesc(Boat boat);

    default Notification safeSave(Notification entity) {
        return save(preapreSave(entity));
    }

    @Query("SELECT o FROM Notification  o WHERE o.boat = :boat AND o.dateFrom<=:dateTo AND o.dateTo>=:dateFrom")
    List<Notification> findByBoatAndDatePeriod(@Param("boat") Boat boat, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    default List<Notification> findByBoatAndDatePeriod(Boat boat, LocalDate date) {
        return findByBoatAndDatePeriod(boat, date, date);
    }
}
