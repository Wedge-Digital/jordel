package com.bloodbowlclub.game.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity - Message sent in a game room
 */
public class GameMessage {
    private final MessageId id;
    private final RoomId roomId;
    private final ParticipantId senderId;
    private final String content;
    private final MessageType type;
    private final Instant timestamp;
    private final int sequenceNumber;

    private GameMessage(
        MessageId id,
        RoomId roomId,
        ParticipantId senderId,
        String content,
        MessageType type,
        Instant timestamp,
        int sequenceNumber
    ) {
        this.id = Objects.requireNonNull(id, "Message id cannot be null");
        this.roomId = Objects.requireNonNull(roomId, "Room id cannot be null");
        this.senderId = Objects.requireNonNull(senderId, "Sender id cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.type = Objects.requireNonNull(type, "Message type cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.sequenceNumber = sequenceNumber;
    }

    public static GameMessage create(
        RoomId roomId,
        ParticipantId senderId,
        String content,
        MessageType type,
        int sequenceNumber
    ) {
        return new GameMessage(
            MessageId.generate(),
            roomId,
            senderId,
            content,
            type,
            Instant.now(),
            sequenceNumber
        );
    }

    public MessageId getId() { return id; }
    public RoomId getRoomId() { return roomId; }
    public ParticipantId getSenderId() { return senderId; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public Instant getTimestamp() { return timestamp; }
    public int getSequenceNumber() { return sequenceNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMessage that = (GameMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}