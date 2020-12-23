package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExitNotificationRepository extends JpaRepository<ExitNotification, Long>, JpaSpecificationExecutor<ExitNotification>, ExitNotificationSafeSaveRepository {

    default ExitNotification safeSave(ExitNotification entity) {
        return save(prepareSave(entity));
    }


    @Query(value = "SELECT t.* FROM  exit_notification t WHERE t.id_boat=:idBoat AND t.exit_date_time<=:dateTo AND (t.return_date_time>=:dateFrom OR t.return_date_time IS NULL) ORDER BY t.exit_date_time DESC LIMIT 1", nativeQuery = true)
    Optional<ExitNotification> findLastByBoatAndPeriod(@Param("idBoat") Long idBoat, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);


    default Optional<ExitNotification> findLastByBoatAndPeriod(Boat boat, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return findLastByBoatAndPeriod(boat.getId(), dateFrom, dateTo);
    }

    default Optional<ExitNotification> findLastByBoatAndPeriod(Boat boat, LocalDate dateFrom, LocalDate dateTo) {
        return findLastByBoatAndPeriod(boat, dateFrom.atStartOfDay(), dateTo.plusDays(1L).atStartOfDay());
    }

    default Optional<ExitNotification> findLastByBoatAndPeriod(Boat boat, LocalDate date) {
        return findLastByBoatAndPeriod(boat, date, date);
    }

    default Optional<ExitNotification> findLastByBoatAndPeriod(Boat boat) {
        return findLastByBoatAndPeriod(boat, LocalDate.now());
    }

    default Optional<ExitNotification> findLast(Notification notification) {
        return findLastByBoatAndPeriod(notification.getBoat(), notification.getDateFrom(), notification.getDateTo());
    }


    @Transactional
    default ExitNotification addCallExit(Notification notification, LocalDateTime callExitDate) {
        return addCallExit(new ExitNotification(), notification, callExitDate);
    }

    @Transactional
    default ExitNotification addCallExit(ExitNotification exitNotification, Notification notification, LocalDateTime callExitDate) {
        if (exitNotification == null) {
            exitNotification = new ExitNotification();
        }
        if (notification.isValidExit(callExitDate)) {
            exitNotification.setBoat(notification.getBoat());
            exitNotification.setCaptain(notification.getCaptain());
            exitNotification.setRegion(notification.getRegion());
            exitNotification.setPier(notification.getBoat().getPier());
            exitNotification.setUnit(notification.getBoat().getUnit());
            exitNotification.setExitCallDateTime(callExitDate);
            exitNotification.setExitDateTime(callExitDate.plusHours(2));
            return safeSave(exitNotification);
        }
        return null;
    }

    @Transactional
    default Optional<ExitNotification> addReturn(Notification notification) {
        Optional<ExitNotification> exitNotification = findLast(notification);
        exitNotification.ifPresent(e -> {
            e.setReturnCallDateTime(LocalDateTime.now());
            e.setReturnDateTime(LocalDateTime.now());
        });
        exitNotification = Optional.ofNullable(exitNotification.map(this::safeSave).orElse(null));
        return exitNotification;
    }
}
