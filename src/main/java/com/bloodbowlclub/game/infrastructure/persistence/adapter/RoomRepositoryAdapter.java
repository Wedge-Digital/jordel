package com.bloodbowlclub.game.infrastructure.persistence.adapter;

import com.bloodbowlclub.game.domain.model.*;
import com.bloodbowlclub.game.domain.port.RoomRepository;
import com.bloodbowlclub.game.infrastructure.persistence.entity.RoomEntity;
import com.bloodbowlclub.game.infrastructure.persistence.repository.JpaRoomRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter - Implements RoomRepository using JPA
 */
@Component
public class RoomRepositoryAdapter implements RoomRepository {
    
    private final JpaRoomRepository jpaRoomRepository;

    public RoomRepositoryAdapter(JpaRoomRepository jpaRoomRepository) {
        this.jpaRoomRepository = jpaRoomRepository;
    }

    @Override
    public Room save(Room room) {
        RoomEntity entity = RoomEntity.from(room);
        jpaRoomRepository.save(entity);
        return room; // In a real implementation, you'd reconstruct from entity
    }

    @Override
    public Optional<Room> findById(RoomId roomId) {
        return jpaRoomRepository.findById(roomId.getValue())
            .map(RoomEntity::toDomain);
    }

    @Override
    public List<Room> findActiveRooms() {
        return jpaRoomRepository.findActiveRooms()
            .stream()
            .map(RoomEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        return jpaRoomRepository.findByStatus(status)
            .stream()
            .map(RoomEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(RoomId roomId) {
        jpaRoomRepository.deleteById(roomId.getValue());
    }

    @Override
    public boolean exists(RoomId roomId) {
        return jpaRoomRepository.existsById(roomId.getValue());
    }
}
