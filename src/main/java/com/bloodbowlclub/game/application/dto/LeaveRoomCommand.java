package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.ParticipantId;
import com.bloodbowlclub.game.domain.model.RoomId;

public record LeaveRoomCommand(RoomId roomId, ParticipantId participantId) {}