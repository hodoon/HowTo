package com.example.HowToProj.controller;

import com.example.HowToProj.dto.NotificationDto;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public NotificationDto createNotification(@PathVariable Long userId, @RequestBody NotificationDto notificationDto) {
        // User 객체를 생성하고 userId를 설정합니다.
        User user = new User();
        user.setId(userId);

        // NotificationDto를 서비스 메서드에 전달하여 알림을 생성합니다.
        NotificationDto createdNotification = notificationService.createNotification(
                user,
                notificationDto.getContent(),
                notificationDto.getNotificationType()
        );

        // 생성된 알림을 클라이언트에 반환합니다.
        return createdNotification;
    }


    @GetMapping
    public List<NotificationDto> getNotificationsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return notificationService.getNotificationsByUser(user);
    }

    @PutMapping("/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }

}
