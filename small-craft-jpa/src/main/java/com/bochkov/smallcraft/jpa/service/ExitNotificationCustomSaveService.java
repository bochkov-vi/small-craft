package com.bochkov.smallcraft.jpa.service;

import com.bochkov.smallcraft.jpa.entity.ExitNotification;
import com.bochkov.smallcraft.jpa.entity.Notification;

public interface ExitNotificationCustomSaveService {

    ExitNotification save(ExitNotification entity);
}
