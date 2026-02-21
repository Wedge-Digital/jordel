package com.bloodbowlclub.game.infrastructure.persistence.repository;

import com.bloodbowlclub.game.domain.model.RoomStatus;
import com.bloodbowlclub.game.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Room
 */
@Repository
public interface JpaRoomRepository extends JpaRepository<RoomEntity, String> {
    
    @Query("SELECT r FROM RoomEntity r WHERE r.status != 'CLOSED'")
    List<RoomEntity> findActiveRooms();
    
    List<RoomEntity> findByStatus(RoomStatus status);
}
