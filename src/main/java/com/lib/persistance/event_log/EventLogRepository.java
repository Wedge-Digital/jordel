package com.lib.persistance.event_log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLogEntity, String> {

    List<EventLogEntity> findBySubject(String agregateId);

    List<EventLogEntity> findBySource(String userId);
}