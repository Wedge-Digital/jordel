package com.bloodbowlclub.lib.services.email_service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    void sendEmail(EmailRequest request);
}