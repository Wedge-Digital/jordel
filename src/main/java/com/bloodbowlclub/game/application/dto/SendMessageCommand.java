package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.MessageType;
import com.bloodbowlclub.game.domain.model.ParticipantId;
import com.bloodbowlclub.game.domain.model.RoomId;

public record SendMessageCommand(
    RoomId roomId,
    ParticipantId senderId,
    String content,
    MessageType type
) {}