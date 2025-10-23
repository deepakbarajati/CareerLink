package com.careerLink.notification_service.consumer;

import com.careerLink.notification_service.clients.ConnectionsClient;
import com.careerLink.notification_service.dto.PersonDto;
import com.careerLink.notification_service.entity.Notification;
import com.careerLink.notification_service.repository.NotificationRepository;
import com.careerLink.notification_service.service.SendNotification;
import com.careerLink.post_service.event.PostCreatedEvent;
import com.careerLink.post_service.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final SendNotification sendNotification;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent){
        log.info("Sending Notifications: handlePostCreated: {}",postCreatedEvent);
        List<PersonDto> connections= connectionsClient.getFirstConnections(postCreatedEvent.getCreatorId());

        for(PersonDto connection:connections){
            sendNotification.send(connection.getUserId(),"Your Connection "+postCreatedEvent.getCreatorId()+" has created " +
                    "a post, Check it out");
        }

    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent){
        log.info("Sending Notifications: handlePostLiked: {}",postLikedEvent);

        String message = String.format("Your post ,%d has been liked by %d",postLikedEvent.getPostId(),
                postLikedEvent.getLikedByUserId());

        sendNotification.send(postLikedEvent.getCreatorId(), message);
    }


}
