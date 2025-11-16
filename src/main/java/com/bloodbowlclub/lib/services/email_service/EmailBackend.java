package com.bloodbowlclub.lib.services.email_service;

public interface EmailBackend {
    void sendEmail(EmailRequest request) throws Exception;
    String getName();
}
