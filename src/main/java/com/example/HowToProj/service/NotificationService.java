package com.example.HowToProj.service;

import com.example.HowToProj.dto.NotificationDto;
import com.example.HowToProj.entity.Notification;
import com.example.HowToProj.entity.User;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public NotificationDto createNotification(User user, String content, Notification.NotificationType notificationType) {
        Notification notification = Notification.builder()
                .user(user)
                .content(content)
                .notificationType(notificationType)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationDto.fromEntity(savedNotification);
    }

    public List<NotificationDto> getNotificationsByUser(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream().map(NotificationDto::fromEntity).collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotificationsByUser(User user) {
        List<Notification> notifications = notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
        return notifications.stream().map(NotificationDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setRead(true);
            notification.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        } else {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
    }
}
