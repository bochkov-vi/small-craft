package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExitNotificationRepository extends JpaRepository<ExitNotification, Long>, JpaSpecificationExecutor<ExitNotification>, ExitNotificationSafeSaveRepository {

    default ExitNotification safeSave(ExitNotification entity) {
        return save(prepareSave(entity));
    }

    @Query("SELECT o FROM ExitNotification o WHERE o.boat=:boat AND o.exitDateTime<=:dateTo AND (o.returnDateTime>=:dateFrom OR o.returnDateTime IS NOT NULL)")
    Optional<ExitNotification> findByBoatAndPeriod(@Param("boat") Boat boat, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

    default Optional<ExitNotification> findByBoatAndPeriod(Boat boat, LocalDate dateFrom, LocalDate dateTo) {
        return findByBoatAndPeriod(boat, dateFrom.atStartOfDay(), dateTo.plusDays(1L).atStartOfDay());
    }

    default Optional<ExitNotification> findByBoatAndPeriod(Boat boat, LocalDate date) {
        return findByBoatAndPeriod(boat, date, date);
    }

    default Optional<ExitNotification> findByBoatAndPeriod(Boat boat) {
        return findByBoatAndPeriod(boat, LocalDate.now());
    }
}
