package com.bloodbowlclub.game.application.service;

import com.bloodbowlclub.game.application.dto.*;
import com.bloodbowlclub.game.domain.model.*;
import com.bloodbowlclub.game.domain.port.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service - Message handling and relay
 */
@Service
@Transactional
public class MessageService {
    
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final WebSocketMessaging webSocketMessaging;

    public MessageService(
        RoomRepository roomRepository,
        MessageRepository messageRepository,
        WebSocketMessaging webSocketMessaging
    ) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.webSocketMessaging = webSocketMessaging;
    }

    /**
     * Sends a message in a room and relays it to all participants
     */
    public MessageDTO sendMessage(SendMessageCommand command) {
        Room room = roomRepository.findById(command.roomId())
            .orElseThrow(() -> new RoomNotFoundException(command.roomId()));
        
        GameMessage message = room.sendMessage(
            command.senderId(),
            command.content(),
            command.type()
        );
        
        // Persist the message for replay
        messageRepository.save(message);
        
        // Update room state
        roomRepository.save(room);
        
        // Relay message to all participants via WebSocket
        MessageDTO messageDTO = MessageDTO.from(message);
        webSocketMessaging.broadcastToRoom(command.roomId(), messageDTO);
        
        return messageDTO;
    }

    /**
     * Retrieves message history for a room (for replay)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessageHistory(RoomId roomId) {
        return messageRepository.findByRoomIdOrderBySequence(roomId)
            .stream()
            .map(MessageDTO::from)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves message history from a specific sequence number
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessageHistoryFrom(RoomId roomId, int fromSequence) {
        return messageRepository.findByRoomIdFromSequence(roomId, fromSequence)
            .stream()
            .map(MessageDTO::from)
            .collect(Collectors.toList());
    }

    /**
     * Gets the total message count for a room
     */
    @Transactional(readOnly = true)
    public long getMessageCount(RoomId roomId) {
        return messageRepository.countByRoomId(roomId);
    }
}
