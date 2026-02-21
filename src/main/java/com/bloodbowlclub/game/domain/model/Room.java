package com.bloodbowlclub.game.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Room aggregate root - manages game room lifecycle and participants
 */
public class Room {
    private final RoomId id;
    private final String name;
    private final Instant createdAt;
    private RoomStatus status;
    private final List<Player> players;
    private final List<Spectator> spectators;
    private final List<GameMessage> messageHistory;
    private static final int MAX_PLAYERS = 2;

    private Room(RoomId id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = Instant.now();
        this.status = RoomStatus.WAITING;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.messageHistory = new ArrayList<>();
    }

    public static Room create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        return new Room(RoomId.generate(), name);
    }

    /**
     * Joins a participant to the room
     * Returns JoinResult indicating the role assigned (PLAYER or SPECTATOR)
     */
    public JoinResult join(ParticipantId participantId, String displayName) {
        validateNotClosed();
        
        // Check if already in room
        if (isParticipant(participantId)) {
            throw new IllegalStateException("Participant already in room");
        }

        if (players.size() < MAX_PLAYERS) {
            Player player = new Player(participantId, displayName, players.size() + 1);
            players.add(player);
            
            if (players.size() == MAX_PLAYERS) {
                this.status = RoomStatus.READY;
            }
            
            return JoinResult.asPlayer(player);
        } else {
            Spectator spectator = new Spectator(participantId, displayName);
            spectators.add(spectator);
            return JoinResult.asSpectator(spectator);
        }
    }

    /**
     * Records a message sent in the room
     */
    public GameMessage sendMessage(ParticipantId senderId, String content, MessageType type) {
        validateNotClosed();
        
        if (!isParticipant(senderId)) {
            throw new IllegalStateException("Sender is not in the room");
        }

        GameMessage message = GameMessage.create(
            id,
            senderId,
            content,
            type,
            messageHistory.size() + 1
        );
        
        messageHistory.add(message);
        return message;
    }

    /**
     * Starts the game - only possible when room is READY
     */
    public void startGame() {
        if (status != RoomStatus.READY) {
            throw new IllegalStateException("Cannot start game - room not ready");
        }
        this.status = RoomStatus.IN_PROGRESS;
    }

    /**
     * Ends the game
     */
    public void endGame() {
        if (status != RoomStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot end game - game not in progress");
        }
        this.status = RoomStatus.FINISHED;
    }

    /**
     * Closes the room - no more participants can join
     */
    public void close() {
        this.status = RoomStatus.CLOSED;
    }

    /**
     * Removes a participant from the room
     */
    public void removeParticipant(ParticipantId participantId) {
        players.removeIf(p -> p.getId().equals(participantId));
        spectators.removeIf(s -> s.getId().equals(participantId));
        
        // If we lost players, revert to WAITING
        if (players.size() < MAX_PLAYERS && status == RoomStatus.READY) {
            this.status = RoomStatus.WAITING;
        }
        
        // Close room if no participants left
        if (players.isEmpty() && spectators.isEmpty()) {
            close();
        }
    }

    private boolean isParticipant(ParticipantId participantId) {
        return players.stream().anyMatch(p -> p.getId().equals(participantId))
            || spectators.stream().anyMatch(s -> s.getId().equals(participantId));
    }

    private void validateNotClosed() {
        if (status == RoomStatus.CLOSED) {
            throw new IllegalStateException("Room is closed");
        }
    }

    // Getters
    public RoomId getId() { return id; }
    public String getName() { return name; }
    public RoomStatus getStatus() { return status; }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public List<Spectator> getSpectators() { return Collections.unmodifiableList(spectators); }
    public List<GameMessage> getMessageHistory() { return Collections.unmodifiableList(messageHistory); }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isFull() { return players.size() >= MAX_PLAYERS; }
    public int getPlayerCount() { return players.size(); }
    public int getSpectatorCount() { return spectators.size(); }
}
