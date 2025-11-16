package com.bloodbowlclub.lib.services.email_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EmailRepository extends JpaRepository<EmailLog, Long> {
}

// EmailLog.java

