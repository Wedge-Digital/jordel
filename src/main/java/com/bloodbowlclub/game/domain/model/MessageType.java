package com.bloodbowlclub.game.domain.model;

/**
 * Enum - Message type classification
 */
public enum MessageType {
    CHAT,           // Chat message
    GAME_ACTION,    // Game action (move, dice roll, etc.)
    SYSTEM,         // System notification
    JOIN,           // Player/spectator joined
    LEAVE           // Player/spectator left
}