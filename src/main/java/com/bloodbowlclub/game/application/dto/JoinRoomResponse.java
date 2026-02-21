package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.ParticipantRole;

public record JoinRoomResponse(
    String participantId,
    ParticipantRole role,
    Integer playerNumber,
    RoomDTO room
) {}