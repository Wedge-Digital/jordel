package com.bloodbowlclub.game.domain.model;

/**
 * Value Object - Result of joining a room
 */
public class JoinResult {
    private final ParticipantRole role;
    private final ParticipantId participantId;
    private final String displayName;
    private final Integer playerNumber; // Only set for PLAYER role

    private JoinResult(ParticipantRole role, ParticipantId participantId, String displayName, Integer playerNumber) {
        this.role = role;
        this.participantId = participantId;
        this.displayName = displayName;
        this.playerNumber = playerNumber;
    }

    public static JoinResult asPlayer(Player player) {
        return new JoinResult(
            ParticipantRole.PLAYER,
            player.getId(),
            player.getDisplayName(),
            player.getPlayerNumber()
        );
    }

    public static JoinResult asSpectator(Spectator spectator) {
        return new JoinResult(
            ParticipantRole.SPECTATOR,
            spectator.getId(),
            spectator.getDisplayName(),
            null
        );
    }

    public ParticipantRole getRole() { return role; }
    public ParticipantId getParticipantId() { return participantId; }
    public String getDisplayName() { return displayName; }
    public Integer getPlayerNumber() { return playerNumber; }
    public boolean isPlayer() { return role == ParticipantRole.PLAYER; }
    public boolean isSpectator() { return role == ParticipantRole.SPECTATOR; }
}