package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.RoomId;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(RoomId roomId) {
        super("Room not found: " + roomId.getValue());
    }
}