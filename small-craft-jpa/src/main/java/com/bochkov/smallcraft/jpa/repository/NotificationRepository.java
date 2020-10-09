package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Notification;
import com.bochkov.smallcraft.jpa.entity.NotificationPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, NotificationPK>, JpaSpecificationExecutor<Notification> {

    @Query(nativeQuery = true, value = "SELECT distinct region FROM notification n WHERE region ILIKE :mask ORDER BY position(:sort in region),length(region),region")
    List<String> findRegionByMask(@Param("mask") String mask, @Param("sort") String sort);

    default List<String> findRegionByMask(@Param("mask") String mask) {
        return findRegionByMask(Optional.ofNullable(mask).map(expr -> String.format("%%%s%%", expr)).orElse("%"), Optional.ofNullable(mask).orElse(""));
    }
}
