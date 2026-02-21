package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.RoomStatus;

public record RoomStateChangedEvent(String roomId, RoomStatus newStatus) {}