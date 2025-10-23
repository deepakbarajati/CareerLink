package com.careerLink.connections_service.service;

import com.careerLink.connections_service.entity.Person;
import com.careerLink.connections_service.event.AcceptConnectionRequestEvent;
import com.careerLink.connections_service.event.SendConnectionRequestEvent;
import com.careerLink.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.careerLink.connections_service.auth.UserContextHolder.getCurrentUserId;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConnectionsService {

    private final PersonRepository personRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendRequestKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptRequestKafkaTemplate;

    public List<Person> getFirstDegreeConnections() {
        Long userId = getCurrentUserId();
        log.info("Getting first degree connections for user with id: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public Boolean sendConnectionRequest(Long receiverId) {
        Long senderId=getCurrentUserId();
        log.info("Trying to send connection request, sender: {},receiver: {}",senderId,receiverId);

        if(senderId.equals(receiverId)){
            throw new RuntimeException("Both Sender and receiver are same");
        }
        boolean alreadySentRequest= personRepository.connectionRequestExists(senderId,receiverId);
        if (alreadySentRequest){
            throw new RuntimeException("Connection Request Already Exists, cannot send again");
        }

        boolean alreadyConnected= personRepository.alreadyConnected(senderId,receiverId);
        if (alreadyConnected){
            throw new RuntimeException("Already Connected Users, cannot add connection request");
        }

        log.info("Successfully send the connection request");
        personRepository.addConnectionRequest(senderId,receiverId);

        SendConnectionRequestEvent sendConnectionRequestEvent= SendConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();

        sendRequestKafkaTemplate.send("send-connection-request-topic",sendConnectionRequestEvent);
        return true;
    }

    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverId=getCurrentUserId();

        boolean connectionRequestExists= personRepository.connectionRequestExists(senderId,receiverId);
        if (!connectionRequestExists){
            throw new RuntimeException("No connection request exists to accept");
        }

        personRepository.acceptConnectionRequest(senderId,receiverId);

        log.info("Successfully accepted the connection request, sender: {}, receiver: {}",senderId,receiverId);

        AcceptConnectionRequestEvent acceptConnectionRequestEvent= AcceptConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();

        acceptRequestKafkaTemplate.send("accept-connection-request-topic",acceptConnectionRequestEvent);
        return true;
    }

    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiverId=getCurrentUserId();

        boolean connectionRequestExists= personRepository.connectionRequestExists(senderId,receiverId);
        if (!connectionRequestExists){
            throw new RuntimeException("No connection request exists, cannot delete");
        }

        personRepository.rejectConnectionRequest(senderId,receiverId);

        return true;
    }
}

