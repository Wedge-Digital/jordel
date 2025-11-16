package com.bloodbowlclub.lib.services.email_service;

// EmailConfig.java
import com.bloodbowlclub.lib.services.email_service.backends.BrevoBackend;
import com.bloodbowlclub.lib.services.email_service.backends.ConsoleBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

public class EmailConfig {

    @Bean
    public EmailBackend consoleBackend() {
        return new ConsoleBackend();
    }

    // In a real implementation, BrevoBackend would be configured with actual API keys
// This is just for demonstration
    @Bean
    public EmailBackend brevoBackend() {
        return new BrevoBackend();
    }
}
