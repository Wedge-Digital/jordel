package com.bloodbowlclub.game.domain.model;

/**
 * Enum - Room status lifecycle
 */
public enum RoomStatus {
    WAITING,      // Waiting for players
    READY,        // 2 players joined, ready to start
    IN_PROGRESS,  // Game is running
    FINISHED,     // Game completed
    CLOSED        // Room closed, no more joins allowed
}