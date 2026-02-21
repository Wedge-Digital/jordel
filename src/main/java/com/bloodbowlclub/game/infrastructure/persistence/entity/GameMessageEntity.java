package com.bloodbowlclub.game.infrastructure.persistence.entity;

import com.bloodbowlclub.game.domain.model.*;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA Entity for GameMessage persistence
 */
@Entity
@Table(
    name = "game_messages",
    indexes = {
        @Index(name = "idx_room_sequence", columnList = "roomId,sequenceNumber"),
        @Index(name = "idx_room_timestamp", columnList = "roomId,timestamp")
    }
)
public class GameMessageEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String roomId;
    
    @Column(nullable = false)
    private String senderId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;
    
    @Column(nullable = false)
    private Instant timestamp;
    
    @Column(nullable = false)
    private int sequenceNumber;

    protected GameMessageEntity() {}

    public GameMessageEntity(
        String id,
        String roomId,
        String senderId,
        String content,
        MessageType type,
        Instant timestamp,
        int sequenceNumber
    ) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.sequenceNumber = sequenceNumber;
    }

    public static GameMessageEntity from(GameMessage message) {
        return new GameMessageEntity(
            message.getId().toString(),
            message.getRoomId().getValue(),
            message.getSenderId().getValue(),
            message.getContent(),
            message.getType(),
            message.getTimestamp(),
            message.getSequenceNumber()
        );
    }

    // Note: Domain reconstruction would require a factory method on GameMessage
    // For now, this is a simplified approach

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
}
