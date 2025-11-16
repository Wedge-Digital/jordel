package com.bloodbowlclub.lib.services.email_service;

// EmailQueueService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

public class EmailQueueService {

    private EmailService emailService;

    public CompletableFuture<Void> queueEmail(EmailRequest request) {
        return emailService.sendEmailAsync(request);
    }
}
