package com.bloodbowlclub.lib.services.email_service;

// EmailQueueService.java
import java.util.concurrent.CompletableFuture;

public class EmailQueueService {

    private EmailService emailService;

    public void queueEmail(EmailRequest request) {
        emailService.sendEmail(request);
    }
}
