package com.bochkov.smallcraft.jpa.repository;

import com.bochkov.smallcraft.jpa.entity.Notification;

 interface NotificationSafeSaveRepository {

    Notification preapreSave(Notification entity);
}
