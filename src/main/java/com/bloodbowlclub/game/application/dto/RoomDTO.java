package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO - Room data transfer object
 */
public record RoomDTO(
    String id,
    String name,
    RoomStatus status,
    List<PlayerDTO> players,
    List<SpectatorDTO> spectators,
    int playerCount,
    int spectatorCount,
    boolean full,
    Instant createdAt
) {
    public static RoomDTO from(Room room) {
        return new RoomDTO(
            room.getId().getValue(),
            room.getName(),
            room.getStatus(),
            room.getPlayers().stream()
                .map(PlayerDTO::from)
                .collect(Collectors.toList()),
            room.getSpectators().stream()
                .map(SpectatorDTO::from)
                .collect(Collectors.toList()),
            room.getPlayerCount(),
            room.getSpectatorCount(),
            room.isFull(),
            room.getCreatedAt()
        );
    }
}

/**
 * DTO - Player data
 */
record PlayerDTO(
    String id,
    String displayName,
    int playerNumber
) {
    public static PlayerDTO from(Player player) {
        return new PlayerDTO(
            player.getId().getValue(),
            player.getDisplayName(),
            player.getPlayerNumber()
        );
    }
}

/**
 * DTO - Spectator data
 */
record SpectatorDTO(
    String id,
    String displayName
) {
    public static SpectatorDTO from(Spectator spectator) {
        return new SpectatorDTO(
            spectator.getId().getValue(),
            spectator.getDisplayName()
        );
    }
}