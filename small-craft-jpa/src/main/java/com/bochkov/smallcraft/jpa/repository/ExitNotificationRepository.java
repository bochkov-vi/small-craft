package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExitNotificationRepository extends JpaRepository<ExitNotification, Long>, JpaSpecificationExecutor<ExitNotification> {

}
