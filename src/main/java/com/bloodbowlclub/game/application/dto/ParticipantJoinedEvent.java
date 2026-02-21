package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.ParticipantRole;

public record ParticipantJoinedEvent(
    String participantId,
    String displayName,
    ParticipantRole role,
    Integer playerNumber
) {}