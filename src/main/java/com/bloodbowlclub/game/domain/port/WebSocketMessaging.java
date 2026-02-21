package com.bloodbowlclub.game.domain.port;

import com.bloodbowlclub.game.domain.model.*;

/**
 * Port - WebSocket messaging for real-time communication
 */
public interface WebSocketMessaging {
    
    /**
     * Sends a message to all participants in a room
     */
    void broadcastToRoom(RoomId roomId, Object message);
    
    /**
     * Sends a message to a specific participant
     */
    void sendToParticipant(ParticipantId participantId, Object message);
    
    /**
     * Sends a message to all players in a room (excludes spectators)
     */
    void broadcastToPlayers(RoomId roomId, Object message);
    
    /**
     * Sends a message to all spectators in a room
     */
    void broadcastToSpectators(RoomId roomId, Object message);
    
    /**
     * Notifies about room state change
     */
    void notifyRoomStateChange(RoomId roomId, RoomStatus newStatus);
}
