package com.careerLink.notification_service.service;


import com.careerLink.notification_service.entity.Notification;
import com.careerLink.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendNotification {

    private final NotificationRepository notificationRepository;

    public void send(Long userId, String message){

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }
}
