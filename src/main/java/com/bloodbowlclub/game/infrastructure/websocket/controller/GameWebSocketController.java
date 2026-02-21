package com.bloodbowlclub.game.infrastructure.websocket.controller;

import com.bloodbowlclub.game.application.dto.*;
import com.bloodbowlclub.game.application.service.MessageService;
import com.bloodbowlclub.game.application.service.RoomService;
import com.bloodbowlclub.game.domain.model.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket Controller - Handles incoming WebSocket messages
 */
@Controller
public class GameWebSocketController {
    
    private final RoomService roomService;
    private final MessageService messageService;

    public GameWebSocketController(RoomService roomService, MessageService messageService) {
        this.roomService = roomService;
        this.messageService = messageService;
    }

    /**
     * Handles message sending from clients
     * Messages are automatically broadcast to the room via MessageService
     */
    @MessageMapping("/room/{roomId}/send")
    public void handleMessage(
        @DestinationVariable String roomId,
        @Payload SendMessageRequest request,
        Principal principal
    ) {
        SendMessageCommand command = new SendMessageCommand(
            RoomId.of(roomId),
            ParticipantId.of(request.senderId()),
            request.content(),
            request.type()
        );
        
        // The service will persist and broadcast the message
        messageService.sendMessage(command);
    }

    /**
     * Handles game action messages
     */
    @MessageMapping("/room/{roomId}/action")
    public void handleGameAction(
        @DestinationVariable String roomId,
        @Payload GameActionRequest request
    ) {
        SendMessageCommand command = new SendMessageCommand(
            RoomId.of(roomId),
            ParticipantId.of(request.playerId()),
            request.action(),
            MessageType.GAME_ACTION
        );
        
        messageService.sendMessage(command);
    }

    /**
     * Handles chat messages
     */
    @MessageMapping("/room/{roomId}/chat")
    public void handleChat(
        @DestinationVariable String roomId,
        @Payload ChatMessageRequest request
    ) {
        SendMessageCommand command = new SendMessageCommand(
            RoomId.of(roomId),
            ParticipantId.of(request.senderId()),
            request.message(),
            MessageType.CHAT
        );
        
        messageService.sendMessage(command);
    }

    /**
     * Handles start game request
     */
    @MessageMapping("/room/{roomId}/start")
    public void handleStartGame(@DestinationVariable String roomId) {
        roomService.startGame(new StartGameCommand(RoomId.of(roomId)));
    }

    /**
     * Exception handler for WebSocket errors
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorMessage handleException(Exception exception) {
        return new ErrorMessage(exception.getMessage());
    }

    // Request DTOs for WebSocket
    public record SendMessageRequest(String senderId, String content, MessageType type) {}
    public record GameActionRequest(String playerId, String action) {}
    public record ChatMessageRequest(String senderId, String message) {}
    public record ErrorMessage(String error) {}
}
