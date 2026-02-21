package com.bloodbowlclub.game.application.dto;

import com.bloodbowlclub.game.domain.model.RoomId;

public record StartGameCommand(RoomId roomId) {}