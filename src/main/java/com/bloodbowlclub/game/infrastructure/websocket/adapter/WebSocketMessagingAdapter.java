package com.bloodbowlclub.game.infrastructure.websocket.adapter;

import com.bloodbowlclub.game.domain.model.*;
import com.bloodbowlclub.game.domain.port.WebSocketMessaging;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adapter - Implements WebSocketMessaging using Spring WebSocket
 */
@Component
public class WebSocketMessagingAdapter implements WebSocketMessaging {
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketMessagingAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcastToRoom(RoomId roomId, Object message) {
        // Send to all subscribers of the room topic
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId.getValue(),
            message
        );
    }

    @Override
    public void sendToParticipant(ParticipantId participantId, Object message) {
        // Send to specific participant's queue
        messagingTemplate.convertAndSendToUser(
            participantId.getValue(),
            "/queue/messages",
            message
        );
    }

    @Override
    public void broadcastToPlayers(RoomId roomId, Object message) {
        // Send to players-only topic
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId.getValue() + "/players",
            message
        );
    }

    @Override
    public void broadcastToSpectators(RoomId roomId, Object message) {
        // Send to spectators-only topic
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId.getValue() + "/spectators",
            message
        );
    }

    @Override
    public void notifyRoomStateChange(RoomId roomId, RoomStatus newStatus) {
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId.getValue() + "/state",
            new RoomStateChangeNotification(roomId.getValue(), newStatus)
        );
    }

    private record RoomStateChangeNotification(String roomId, RoomStatus newStatus) {}
}
