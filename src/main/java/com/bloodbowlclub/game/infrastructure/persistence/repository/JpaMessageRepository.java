package com.bloodbowlclub.game.infrastructure.persistence.repository;

import com.bloodbowlclub.game.infrastructure.persistence.entity.GameMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for GameMessage
 */
@Repository
public interface JpaMessageRepository extends JpaRepository<GameMessageEntity, String> {
    
    List<GameMessageEntity> findByRoomIdOrderBySequenceNumberAsc(String roomId);
    
    @Query("SELECT m FROM GameMessageEntity m WHERE m.roomId = :roomId AND m.sequenceNumber >= :fromSequence ORDER BY m.sequenceNumber ASC")
    List<GameMessageEntity> findByRoomIdFromSequence(
        @Param("roomId") String roomId, 
        @Param("fromSequence") int fromSequence
    );
    
    long countByRoomId(String roomId);
    
    void deleteByRoomId(String roomId);
}
