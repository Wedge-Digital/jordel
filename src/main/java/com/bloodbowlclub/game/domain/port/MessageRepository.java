package com.bloodbowlclub.game.domain.port;

import com.bloodbowlclub.game.domain.model.*;

import java.util.List;

/**
 * Port - Repository for GameMessage persistence (for replay)
 */
public interface MessageRepository {
    
    /**
     * Saves a message
     */
    GameMessage save(GameMessage message);
    
    /**
     * Saves multiple messages in batch
     */
    List<GameMessage> saveAll(List<GameMessage> messages);
    
    /**
     * Retrieves all messages for a room, ordered by sequence number
     */
    List<GameMessage> findByRoomIdOrderBySequence(RoomId roomId);
    
    /**
     * Retrieves messages for a room starting from a specific sequence number
     */
    List<GameMessage> findByRoomIdFromSequence(RoomId roomId, int fromSequence);
    
    /**
     * Counts messages in a room
     */
    long countByRoomId(RoomId roomId);
    
    /**
     * Deletes all messages for a room
     */
    void deleteByRoomId(RoomId roomId);
}
