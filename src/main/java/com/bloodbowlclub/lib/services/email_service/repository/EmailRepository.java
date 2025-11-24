package com.bloodbowlclub.lib.services.email_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailLog, Long> {
}


