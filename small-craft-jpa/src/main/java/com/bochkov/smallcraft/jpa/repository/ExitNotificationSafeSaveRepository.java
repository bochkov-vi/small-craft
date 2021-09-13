package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;

 interface ExitNotificationSafeSaveRepository {

    ExitNotification prepareSave(ExitNotification entity);
}
