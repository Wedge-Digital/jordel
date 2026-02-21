package com.bloodbowlclub.game.infrastructure.rest.controller;

import com.bloodbowlclub.game.application.dto.*;
import com.bloodbowlclub.game.application.service.MessageService;
import com.bloodbowlclub.game.application.service.RoomService;
import com.bloodbowlclub.game.domain.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Room management
 */
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {
    
    private final RoomService roomService;
    private final MessageService messageService;

    public RoomController(RoomService roomService, MessageService messageService) {
        this.roomService = roomService;
        this.messageService = messageService;
    }

    /**
     * Creates a new room
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody CreateRoomRequest request) {
        CreateRoomCommand command = new CreateRoomCommand(request.name());
        RoomDTO room = roomService.createRoom(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /**
     * Lists all active rooms
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<List<RoomDTO>> listRooms() {
        List<RoomDTO> rooms = roomService.listActiveRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Gets room details
     * GET /api/rooms/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable String roomId) {
        RoomDTO room = roomService.getRoom(RoomId.of(roomId));
        return ResponseEntity.ok(room);
    }

    /**
     * Joins a room
     * POST /api/rooms/{roomId}/join
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(
        @PathVariable String roomId,
        @RequestBody JoinRoomRequest request
    ) {
        JoinRoomCommand command = new JoinRoomCommand(
            RoomId.of(roomId),
            request.displayName()
        );
        JoinRoomResponse response = roomService.joinRoom(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Leaves a room
     * POST /api/rooms/{roomId}/leave
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(
        @PathVariable String roomId,
        @RequestBody LeaveRoomRequest request
    ) {
        LeaveRoomCommand command = new LeaveRoomCommand(
            RoomId.of(roomId),
            ParticipantId.of(request.participantId())
        );
        roomService.leaveRoom(command);
        return ResponseEntity.noContent().build();
    }

    /**
     * Starts a game
     * POST /api/rooms/{roomId}/start
     */
    @PostMapping("/{roomId}/start")
    public ResponseEntity<Void> startGame(@PathVariable String roomId) {
        roomService.startGame(new StartGameCommand(RoomId.of(roomId)));
        return ResponseEntity.ok().build();
    }

    /**
     * Ends a game
     * POST /api/rooms/{roomId}/end
     */
    @PostMapping("/{roomId}/end")
    public ResponseEntity<Void> endGame(@PathVariable String roomId) {
        roomService.endGame(new EndGameCommand(RoomId.of(roomId)));
        return ResponseEntity.ok().build();
    }

    /**
     * Gets message history for replay
     * GET /api/rooms/{roomId}/messages
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<MessageHistoryResponse> getMessageHistory(
        @PathVariable String roomId,
        @RequestParam(required = false) Integer fromSequence
    ) {
        List<MessageDTO> messages;
        if (fromSequence != null) {
            messages = messageService.getMessageHistoryFrom(RoomId.of(roomId), fromSequence);
        } else {
            messages = messageService.getMessageHistory(RoomId.of(roomId));
        }
        
        long totalCount = messageService.getMessageCount(RoomId.of(roomId));
        
        return ResponseEntity.ok(new MessageHistoryResponse(messages, totalCount));
    }

    // Request/Response DTOs
    public record CreateRoomRequest(String name) {}
    public record JoinRoomRequest(String displayName) {}
    public record LeaveRoomRequest(String participantId) {}
    public record MessageHistoryResponse(List<MessageDTO> messages, long totalCount) {}

    /**
     * Exception handler
     */
    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFound(RoomNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }

    public record ErrorResponse(String error) {}
}
