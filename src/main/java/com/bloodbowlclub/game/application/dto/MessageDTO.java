package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.GameMessage;
import com.bloodbowlclub.game.domain.model.MessageType;

import java.time.Instant;

/**
 * DTO - Message data
 */
public record MessageDTO(
    String id,
    String roomId,
    String senderId,
    String content,
    MessageType type,
    Instant timestamp,
    int sequenceNumber
) {
    public static MessageDTO from(GameMessage message) {
        return new MessageDTO(
            message.getId().toString(),
            message.getRoomId().getValue(),
            message.getSenderId().getValue(),
            message.getContent(),
            message.getType(),
            message.getTimestamp(),
            message.getSequenceNumber()
        );
    }
}