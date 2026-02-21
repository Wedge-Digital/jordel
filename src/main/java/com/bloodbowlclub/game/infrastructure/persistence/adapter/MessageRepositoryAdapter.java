package com.bloodbowlclub.game.infrastructure.persistence.adapter;

import com.bloodbowlclub.game.domain.model.*;
import com.bloodbowlclub.game.domain.port.MessageRepository;
import com.bloodbowlclub.game.infrastructure.persistence.entity.GameMessageEntity;
import com.bloodbowlclub.game.infrastructure.persistence.repository.JpaMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter - Implements MessageRepository using JPA
 */
@Component
public class MessageRepositoryAdapter implements MessageRepository {
    
    private final JpaMessageRepository jpaMessageRepository;

    public MessageRepositoryAdapter(JpaMessageRepository jpaMessageRepository) {
        this.jpaMessageRepository = jpaMessageRepository;
    }

    @Override
    public GameMessage save(GameMessage message) {
        GameMessageEntity entity = GameMessageEntity.from(message);
        jpaMessageRepository.save(entity);
        return message;
    }

    @Override
    public List<GameMessage> saveAll(List<GameMessage> messages) {
        List<GameMessageEntity> entities = messages.stream()
            .map(GameMessageEntity::from)
            .collect(Collectors.toList());
        
        jpaMessageRepository.saveAll(entities);
        return messages;
    }

    @Override
    public List<GameMessage> findByRoomIdOrderBySequence(RoomId roomId) {
        return jpaMessageRepository.findByRoomIdOrderBySequenceNumberAsc(roomId.getValue())
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<GameMessage> findByRoomIdFromSequence(RoomId roomId, int fromSequence) {
        return jpaMessageRepository.findByRoomIdFromSequence(roomId.getValue(), fromSequence)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByRoomId(RoomId roomId) {
        return jpaMessageRepository.countByRoomId(roomId.getValue());
    }

    @Override
    public void deleteByRoomId(RoomId roomId) {
        jpaMessageRepository.deleteByRoomId(roomId.getValue());
    }

    private GameMessage toDomain(GameMessageEntity entity) {
        // Simplified - in real implementation, use factory method
        return GameMessage.create(
            RoomId.of(entity.getRoomId()),
            ParticipantId.of(entity.getSenderId()),
            entity.getContent(),
            entity.getType(),
            entity.getSequenceNumber()
        );
    }
}
