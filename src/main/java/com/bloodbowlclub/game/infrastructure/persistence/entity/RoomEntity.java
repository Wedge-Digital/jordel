package com.bloodbowlclub.game.infrastructure.persistence.entity;

import com.bloodbowlclub.game.domain.model.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA Entity for Room persistence
 */
@Entity
@Table(name = "rooms")
public class RoomEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @ElementCollection
    @CollectionTable(name = "room_players", joinColumns = @JoinColumn(name = "room_id"))
    private List<PlayerEntity> players = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "room_spectators", joinColumns = @JoinColumn(name = "room_id"))
    private List<SpectatorEntity> spectators = new ArrayList<>();

    protected RoomEntity() {}

    public RoomEntity(String id, String name, RoomStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static RoomEntity from(Room room) {
        RoomEntity entity = new RoomEntity(
            room.getId().getValue(),
            room.getName(),
            room.getStatus(),
            room.getCreatedAt()
        );
        
        entity.players = room.getPlayers().stream()
            .map(PlayerEntity::from)
            .collect(Collectors.toList());
        
        entity.spectators = room.getSpectators().stream()
            .map(SpectatorEntity::from)
            .collect(Collectors.toList());
        
        return entity;
    }

    public Room toDomain() {
        // Use reflection or a builder to reconstruct the Room
        // This is a simplified version - you might need to use a factory method
        throw new UnsupportedOperationException("Domain reconstruction requires factory method");
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<PlayerEntity> getPlayers() { return players; }
    public void setPlayers(List<PlayerEntity> players) { this.players = players; }
    public List<SpectatorEntity> getSpectators() { return spectators; }
    public void setSpectators(List<SpectatorEntity> spectators) { this.spectators = spectators; }
}

@Embeddable
class PlayerEntity {
    private String participantId;
    private String displayName;
    private int playerNumber;

    protected PlayerEntity() {}

    public PlayerEntity(String participantId, String displayName, int playerNumber) {
        this.participantId = participantId;
        this.displayName = displayName;
        this.playerNumber = playerNumber;
    }

    public static PlayerEntity from(Player player) {
        return new PlayerEntity(
            player.getId().getValue(),
            player.getDisplayName(),
            player.getPlayerNumber()
        );
    }

    public Player toDomain() {
        return new Player(
            ParticipantId.of(participantId),
            displayName,
            playerNumber
        );
    }

    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public int getPlayerNumber() { return playerNumber; }
    public void setPlayerNumber(int playerNumber) { this.playerNumber = playerNumber; }
}

@Embeddable
class SpectatorEntity {
    private String participantId;
    private String displayName;

    protected SpectatorEntity() {}

    public SpectatorEntity(String participantId, String displayName) {
        this.participantId = participantId;
        this.displayName = displayName;
    }

    public static SpectatorEntity from(Spectator spectator) {
        return new SpectatorEntity(
            spectator.getId().getValue(),
            spectator.getDisplayName()
        );
    }

    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
