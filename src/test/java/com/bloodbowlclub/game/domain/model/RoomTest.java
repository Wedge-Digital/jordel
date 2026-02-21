package com.bloodbowlclub.game.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Room aggregate
 */
class RoomTest {

    @Test
    void shouldCreateRoomWithValidName() {
        // Given
        String roomName = "Test Room";
        
        // When
        Room room = Room.create(roomName);
        
        // Then
        assertNotNull(room.getId());
        assertEquals(roomName, room.getName());
        assertEquals(RoomStatus.WAITING, room.getStatus());
        assertEquals(0, room.getPlayerCount());
        assertEquals(0, room.getSpectatorCount());
        assertFalse(room.isFull());
    }

    @Test
    void shouldThrowExceptionWhenCreatingRoomWithEmptyName() {
        // Given
        String emptyName = "";
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> Room.create(emptyName));
    }

    @Test
    void shouldAddFirstPlayerSuccessfully() {
        // Given
        Room room = Room.create("Test Room");
        String displayName = "Player1";
        
        // When
        JoinResult result = room.join(ParticipantId.generate(), displayName);
        
        // Then
        assertTrue(result.isPlayer());
        assertEquals(1, result.getPlayerNumber());
        assertEquals(1, room.getPlayerCount());
        assertEquals(RoomStatus.WAITING, room.getStatus());
        assertFalse(room.isFull());
    }

    @Test
    void shouldAddSecondPlayerAndSetRoomToReady() {
        // Given
        Room room = Room.create("Test Room");
        room.join(ParticipantId.generate(), "Player1");
        
        // When
        JoinResult result = room.join(ParticipantId.generate(), "Player2");
        
        // Then
        assertTrue(result.isPlayer());
        assertEquals(2, result.getPlayerNumber());
        assertEquals(2, room.getPlayerCount());
        assertEquals(RoomStatus.READY, room.getStatus());
        assertTrue(room.isFull());
    }

    @Test
    void shouldAddThirdParticipantAsSpectator() {
        // Given
        Room room = Room.create("Test Room");
        room.join(ParticipantId.generate(), "Player1");
        room.join(ParticipantId.generate(), "Player2");
        
        // When
        JoinResult result = room.join(ParticipantId.generate(), "Spectator1");
        
        // Then
        assertTrue(result.isSpectator());
        assertNull(result.getPlayerNumber());
        assertEquals(2, room.getPlayerCount());
        assertEquals(1, room.getSpectatorCount());
        assertEquals(RoomStatus.READY, room.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenParticipantJoinsTwice() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId participantId = ParticipantId.generate();
        room.join(participantId, "Player1");
        
        // When/Then
        assertThrows(IllegalStateException.class, 
            () -> room.join(participantId, "Player1Again"));
    }

    @Test
    void shouldStartGameWhenRoomIsReady() {
        // Given
        Room room = Room.create("Test Room");
        room.join(ParticipantId.generate(), "Player1");
        room.join(ParticipantId.generate(), "Player2");
        
        // When
        room.startGame();
        
        // Then
        assertEquals(RoomStatus.IN_PROGRESS, room.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenStartingGameWithoutEnoughPlayers() {
        // Given
        Room room = Room.create("Test Room");
        room.join(ParticipantId.generate(), "Player1");
        
        // When/Then
        assertThrows(IllegalStateException.class, room::startGame);
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId playerId = ParticipantId.generate();
        room.join(playerId, "Player1");
        
        // When
        GameMessage message = room.sendMessage(playerId, "Hello!", MessageType.CHAT);
        
        // Then
        assertNotNull(message);
        assertEquals("Hello!", message.getContent());
        assertEquals(MessageType.CHAT, message.getType());
        assertEquals(1, message.getSequenceNumber());
        assertEquals(1, room.getMessageHistory().size());
    }

    @Test
    void shouldThrowExceptionWhenNonParticipantSendsMessage() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId outsider = ParticipantId.generate();
        
        // When/Then
        assertThrows(IllegalStateException.class, 
            () -> room.sendMessage(outsider, "Hello!", MessageType.CHAT));
    }

    @Test
    void shouldRemovePlayerAndRevertToWaitingStatus() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId player1Id = ParticipantId.generate();
        ParticipantId player2Id = ParticipantId.generate();
        room.join(player1Id, "Player1");
        room.join(player2Id, "Player2");
        assertEquals(RoomStatus.READY, room.getStatus());
        
        // When
        room.removeParticipant(player2Id);
        
        // Then
        assertEquals(1, room.getPlayerCount());
        assertEquals(RoomStatus.WAITING, room.getStatus());
        assertFalse(room.isFull());
    }

    @Test
    void shouldCloseRoomWhenAllParticipantsLeave() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId playerId = ParticipantId.generate();
        room.join(playerId, "Player1");
        
        // When
        room.removeParticipant(playerId);
        
        // Then
        assertEquals(0, room.getPlayerCount());
        assertEquals(RoomStatus.CLOSED, room.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenJoiningClosedRoom() {
        // Given
        Room room = Room.create("Test Room");
        room.close();
        
        // When/Then
        assertThrows(IllegalStateException.class, 
            () -> room.join(ParticipantId.generate(), "Player1"));
    }

    @Test
    void shouldMaintainMessageSequenceNumbers() {
        // Given
        Room room = Room.create("Test Room");
        ParticipantId playerId = ParticipantId.generate();
        room.join(playerId, "Player1");
        
        // When
        GameMessage msg1 = room.sendMessage(playerId, "First", MessageType.CHAT);
        GameMessage msg2 = room.sendMessage(playerId, "Second", MessageType.CHAT);
        GameMessage msg3 = room.sendMessage(playerId, "Third", MessageType.GAME_ACTION);
        
        // Then
        assertEquals(1, msg1.getSequenceNumber());
        assertEquals(2, msg2.getSequenceNumber());
        assertEquals(3, msg3.getSequenceNumber());
        assertEquals(3, room.getMessageHistory().size());
    }
}
