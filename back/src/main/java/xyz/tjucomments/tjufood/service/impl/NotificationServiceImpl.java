// src/main/java/xyz/tjucomments/tjufood/service/impl/NotificationServiceImpl.java
package xyz.tjucomments.tjufood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.entity.Notification;
import xyz.tjucomments.tjufood.interceptor.UserHolder;
import xyz.tjucomments.tjufood.mapper.NotificationMapper;
import xyz.tjucomments.tjufood.service.INotificationService;
import xyz.tjucomments.tjufood.utils.constants.SystemConstants;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    @Override
    public Result queryMyNotifications(Integer current) {
        Long userId = UserHolder.getUser().getId();
        Page<Notification> page = page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE),
                new QueryWrapper<Notification>().eq("recipient_id", userId).orderByDesc("create_time"));
        return Result.ok(page.getRecords(), page.getTotal());
    }

    @Override
    public Result queryMyUnreadCount() {
        Long userId = UserHolder.getUser().getId();
        long count = count(new QueryWrapper<Notification>()
                .eq("recipient_id", userId)
                .eq("status", 0));
        return Result.ok(count);
    }

    @Override
    public Result readAll() {
        Long userId = UserHolder.getUser().getId();
        update().set("status", 1)
                .eq("recipient_id", userId)
                .eq("status", 0)
                .update();
        return Result.ok();
    }
}