package com.bloodbowlclub.game.application.service;

import com.bloodbowlclub.game.application.dto.*;
import com.bloodbowlclub.game.domain.model.*;
import com.bloodbowlclub.game.domain.port.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service - Room management use cases
 */
@Service
@Transactional
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final WebSocketMessaging webSocketMessaging;

    public RoomService(
        RoomRepository roomRepository,
        MessageRepository messageRepository,
        WebSocketMessaging webSocketMessaging
    ) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.webSocketMessaging = webSocketMessaging;
    }

    /**
     * Creates a new game room
     */
    public RoomDTO createRoom(CreateRoomCommand command) {
        Room room = Room.create(command.name());
        Room savedRoom = roomRepository.save(room);
        
        return RoomDTO.from(savedRoom);
    }

    /**
     * Joins a participant to a room
     */
    public JoinRoomResponse joinRoom(JoinRoomCommand command) {
        Room room = roomRepository.findById(command.roomId())
            .orElseThrow(() -> new RoomNotFoundException(command.roomId()));
        
        ParticipantId participantId = ParticipantId.generate();
        JoinResult result = room.join(participantId, command.displayName());
        
        roomRepository.save(room);
        
        // Send system message about join
        GameMessage joinMessage = room.sendMessage(
            participantId,
            String.format("%s joined as %s", 
                command.displayName(), 
                result.getRole().name().toLowerCase()),
            MessageType.JOIN
        );
        messageRepository.save(joinMessage);
        
        // Notify all participants
        webSocketMessaging.broadcastToRoom(
            command.roomId(),
            new ParticipantJoinedEvent(
                participantId.getValue(),
                command.displayName(),
                result.getRole(),
                result.getPlayerNumber()
            )
        );
        
        return new JoinRoomResponse(
            participantId.getValue(),
            result.getRole(),
            result.getPlayerNumber(),
            RoomDTO.from(room)
        );
    }

    /**
     * Leaves a room
     */
    public void leaveRoom(LeaveRoomCommand command) {
        Room room = roomRepository.findById(command.roomId())
            .orElseThrow(() -> new RoomNotFoundException(command.roomId()));
        
        room.removeParticipant(command.participantId());
        roomRepository.save(room);
        
        // Send system message about leave
        GameMessage leaveMessage = room.sendMessage(
            command.participantId(),
            "Participant left",
            MessageType.LEAVE
        );
        messageRepository.save(leaveMessage);
        
        // Notify remaining participants
        webSocketMessaging.broadcastToRoom(
            command.roomId(),
            new ParticipantLeftEvent(command.participantId().getValue())
        );
    }

    /**
     * Starts a game
     */
    public void startGame(StartGameCommand command) {
        Room room = roomRepository.findById(command.roomId())
            .orElseThrow(() -> new RoomNotFoundException(command.roomId()));
        
        room.startGame();
        roomRepository.save(room);
        
        webSocketMessaging.notifyRoomStateChange(command.roomId(), RoomStatus.IN_PROGRESS);
    }

    /**
     * Ends a game
     */
    public void endGame(EndGameCommand command) {
        Room room = roomRepository.findById(command.roomId())
            .orElseThrow(() -> new RoomNotFoundException(command.roomId()));
        
        room.endGame();
        roomRepository.save(room);
        
        webSocketMessaging.notifyRoomStateChange(command.roomId(), RoomStatus.FINISHED);
    }

    /**
     * Lists all active rooms
     */
    @Transactional(readOnly = true)
    public List<RoomDTO> listActiveRooms() {
        return roomRepository.findActiveRooms()
            .stream()
            .map(RoomDTO::from)
            .collect(Collectors.toList());
    }

    /**
     * Gets room details
     */
    @Transactional(readOnly = true)
    public RoomDTO getRoom(RoomId roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException(roomId));
        
        return RoomDTO.from(room);
    }
}
