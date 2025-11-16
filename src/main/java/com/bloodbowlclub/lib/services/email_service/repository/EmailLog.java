package com.bloodbowlclub.lib.services.email_service.repository;


import com.bloodbowlclub.lib.services.email_service.EmailRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String backendUsed;
    private LocalDateTime sentAt;
    private Boolean success;
    private String errorDetails;

    // Constructors
    public EmailLog() {}

    public EmailLog(EmailRequest request, String backendUsed) {
        this.templateName = request.getTemplateName();
        this.recipientEmail = request.getRecipientEmail();
        this.recipientName = request.getRecipientName();
        this.subject = request.getSubject();
        this.backendUsed = backendUsed;
        this.sentAt = LocalDateTime.now();
        this.success = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBackendUsed() { return backendUsed; }
    public void setBackendUsed(String backendUsed) { this.backendUsed = backendUsed; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }
}