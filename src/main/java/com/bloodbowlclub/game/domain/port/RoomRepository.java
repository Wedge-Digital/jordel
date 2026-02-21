package com.bloodbowlclub.game.domain.port;

import com.bloodbowlclub.game.domain.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Port - Repository for Room aggregate
 */
public interface RoomRepository {
    
    /**
     * Saves a room (create or update)
     */
    Room save(Room room);
    
    /**
     * Finds a room by its ID
     */
    Optional<Room> findById(RoomId roomId);
    
    /**
     * Finds all active rooms (not CLOSED)
     */
    List<Room> findActiveRooms();
    
    /**
     * Finds rooms by status
     */
    List<Room> findByStatus(RoomStatus status);
    
    /**
     * Deletes a room
     */
    void delete(RoomId roomId);
    
    /**
     * Checks if a room exists
     */
    boolean exists(RoomId roomId);
}
