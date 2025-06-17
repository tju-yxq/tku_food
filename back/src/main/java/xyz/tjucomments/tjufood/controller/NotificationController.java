// src/main/java/xyz/tjucomments/tjufood/controller/NotificationController.java
package xyz.tjucomments.tjufood.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import xyz.tjucomments.tjufood.dto.Result;
import xyz.tjucomments.tjufood.service.INotificationService;

@Tag(name = "10. 消息通知", description = "查询和管理用户的消息通知")
@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "authorization")
public class NotificationController {

    @Resource
    private INotificationService notificationService;

    @Operation(summary = "查询我的通知列表")
    @GetMapping("/my")
    public Result listMyNotifications(@Parameter(description = "当前页码") @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return notificationService.queryMyNotifications(current);
    }

    @Operation(summary = "查询我的未读通知数量")
    @GetMapping("/my/unread-count")
    public Result getMyUnreadCount() {
        return notificationService.queryMyUnreadCount();
    }

    @Operation(summary = "将所有通知标记为已读")
    @PutMapping("/read/all")
    public Result readAll() {
        return notificationService.readAll();
    }
}