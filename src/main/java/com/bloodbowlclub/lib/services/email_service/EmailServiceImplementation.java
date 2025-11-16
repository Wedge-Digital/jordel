package com.bloodbowlclub.lib.services.email_service;

// EmailServiceImplementation.java
import com.bloodbowlclub.lib.services.email_service.repository.EmailLog;
import com.bloodbowlclub.lib.services.email_service.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

public class EmailServiceImplementation implements EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailRepository emailRepository;

    @Value("${email.default.sender.name}")
    private String defaultSenderName;

    @Value("${email.default.sender.email}")
    private String defaultSenderEmail;

    private final EmailBackend[] backends;

    public EmailServiceImplementation(EmailBackend... backends) {
        this.backends = backends;
    }

    @Async
    public CompletableFuture<Void> sendEmailAsync(EmailRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Create email log entry
                EmailLog emailLog = new EmailLog(request, "DEFAULT");

                // Process template to generate HTML content
                String htmlContent = templateEngine.processTemplateFromFile(
                        request.getTemplateName(),
                        request.getTemplateVariables()
                );

                // Set default subject if not provided
                if (request.getSubject() == null || request.getSubject().isEmpty()) {
                    request.setSubject("Email from " + defaultSenderName);
                }

                // Send email through the appropriate backend
                EmailBackend backend = findBackend(request);
                if (backend != null) {
                    backend.sendEmail(request);
                    emailLog.setSuccess(true);
                } else {
                    throw new RuntimeException("No suitable backend found for sending email");
                }

                // Save to database
                emailRepository.save(emailLog);

            } catch (Exception e) {
                // Log error and save to database
                EmailLog emailLog = new EmailLog(request, "DEFAULT");
                emailLog.setSuccess(false);
                emailLog.setErrorDetails(e.getMessage());
                emailRepository.save(emailLog);

                throw new RuntimeException("Failed to send email", e);
            }
        });
    }

    private EmailBackend findBackend(EmailRequest request) {
        // In a real implementation, this could be based on:
        // - Configuration
        // - Recipient domain
        // - Priority settings
        // For now, returning the first backend
        return backends.length > 0 ? backends[0] : null;
    }
}