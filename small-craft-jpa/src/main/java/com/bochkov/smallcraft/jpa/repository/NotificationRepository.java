package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Boat;
import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification>, NotificationSafeSaveRepository {

    static Specification<Notification> activeSpecificationAtDate(LocalDate from, LocalDate to) {
        return (r, q, b) -> b.and(b.lessThanOrEqualTo(r.get("dateFrom"), to),
                b.or(b.greaterThanOrEqualTo(r.get("dateTo"), from)));
    }

    static Specification<Notification> activeSpecificationAtDate(LocalDate date) {
        return activeSpecificationAtDate(date, date);
    }

    @Query(nativeQuery = true, value = "SELECT distinct region FROM (SELECT region FROM(SELECT n.region FROM notification_region n WHERE region ILIKE :mask UNION DISTINCT SELECT n.region FROM exit_notification_region n WHERE region ILIKE :mask) as t ORDER BY position(:sort in region), length(region), region) as t",
            countQuery = "SELECT count(distinct region) FROM (SELECT n.region FROM notification_region n WHERE region ILIKE :mask UNION DISTINCT SELECT n.region FROM exit_notification_region n WHERE region ILIKE :mask)as t WHERE :sort IS NOT NULL\n")
    Page<String> findRegionByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findRegionByMask(@Param("mask") String mask, Pageable pg) {
        return findRegionByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    @Query(nativeQuery = true, value = "SELECT distinct activity FROM (SELECT activity FROM(SELECT n.activity FROM notification_activity n WHERE n.activity ILIKE :mask) as t ORDER BY position(:sort in activity), length(activity), activity) as t",
            countQuery = "SELECT count(distinct activity) FROM notification_activity n WHERE activity ILIKE :mask AND :sort IS NOT NULL\n")
    Page<String> findActivityByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findActivityByMask(@Param("mask") String mask, Pageable pg) {
        return findActivityByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    @Query(nativeQuery = true, value = "SELECT distinct time_of_day FROM (SELECT time_of_day FROM(SELECT n.time_of_day FROM notification n WHERE n.time_of_day ILIKE :mask) as t ORDER BY position(:sort in time_of_day), length(time_of_day), time_of_day) as t",
            countQuery = "SELECT count(distinct time_of_day) FROM notification n WHERE time_of_day ILIKE :mask AND :sort IS NOT NULL\n")
    Page<String> findTimeOfDayByMask(@Param("mask") String mask, @Param("sort") String sort, Pageable pg);

    default Page<String> findTimeOfDayByMask(@Param("mask") String mask, Pageable pg) {
        return findTimeOfDayByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""), pg);
    }

    Optional<Notification> findTopByBoatOrderByNumberDesc(Boat boat);

    default Optional<Notification> findTopByBoatOrderByNumberDesc(Boat boat, LocalDate date) {
        return findTopByBoatAndDateToGreaterThanEqualOrderByNumberDesc(boat, date);
    }

    Optional<Notification> findTopByBoatAndDateToGreaterThanEqualOrderByNumberDesc(Boat boat, LocalDate date);

    default Notification safeSave(Notification entity) {
        return save(preapreSave(entity));
    }

    @Query("SELECT o FROM Notification  o WHERE o.boat = :boat AND o.dateFrom<=:dateTo AND o.dateTo>=:dateFrom")
    List<Notification> findByBoatAndDatePeriod(@Param("boat") Boat boat, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    default List<Notification> findByBoatAndDatePeriod(Boat boat, LocalDate date) {
        return findByBoatAndDatePeriod(boat, date, date);
    }

    default List<Notification> findByNumber(Integer number) {
        return findByNumberAndDate(number, LocalDate.now(TimeZone.getTimeZone("Asia/Kamchatka").toZoneId()));
    }

    default List<Notification> findByNumberAndDate(Integer number, LocalDate date) {
        return findByNumberAndPeriod(number, date, date);
    }

    List<Notification> findByNumberAndDateToGreaterThanEqualAndDateFromLessThanEqual(Integer number, LocalDate dateFrom, LocalDate dateTo);

    default List<Notification> findByNumberAndPeriod(Integer number, LocalDate dateFrom, LocalDate dateTo) {
        return findByNumberAndDateToGreaterThanEqualAndDateFromLessThanEqual(number, dateFrom, dateTo);
    }

    List<Notification> findByYearAndNumber(Integer year, Integer number);

    default List<Notification> findByCaptainOrBoatPerson(Person person,LocalDate date) {
        Specification<Notification> specification = (r, q, b) -> b.equal(r.get("captain"), person);
        specification = specification.or((r, q, b) -> b.equal(r.get("boat").get("person"), person));
        specification  = activeSpecificationAtDate(date).and(specification);
        return findAll(specification);
    }

}
