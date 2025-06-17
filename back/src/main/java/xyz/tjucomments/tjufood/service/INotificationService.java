// src/main/java/xyz/tjucomments/tjufood/service/INotificationService.java
package xyz.tjucomments.tjufood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Notification;

public interface INotificationService extends IService<Notification> {
    Result queryMyNotifications(Integer current);
    Result queryMyUnreadCount();
    Result readAll();
}