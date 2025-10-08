package com.auth.io.persistance.write;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessEventRepository extends JpaRepository<BusinessEventEntity, String> {

    List<BusinessEventEntity> findBySource(String eventSource);
}