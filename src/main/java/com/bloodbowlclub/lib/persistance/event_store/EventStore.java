package com.bloodbowlclub.lib.persistance.event_store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EventStore")
public interface EventStore extends JpaRepository<EventEntity, String> {

    List<EventEntity> findBySubject(String agregateId);

    List<EventEntity> findBySource(String userId);


}