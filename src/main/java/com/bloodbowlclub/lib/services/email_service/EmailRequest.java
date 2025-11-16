package com.bloodbowlclub.lib.services.email_service;

import java.util.Map;
import java.util.List;

public class EmailRequest {
    private String templateName;
    private Map<String, Object> templateVariables;
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private List<String> cc;
    private List<String> bcc;

    // Constructeurs
    public EmailRequest() {}

    public EmailRequest(String templateName, Map<String, Object> templateVariables,
                        String recipientEmail, String recipientName) {
        this.templateName = templateName;
        this.templateVariables = templateVariables;
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
    }

    // Getters and Setters
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public Map<String, Object> getTemplateVariables() { return templateVariables; }
    public void setTemplateVariables(Map<String, Object> templateVariables) { this.templateVariables = templateVariables; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public List<String> getCc() { return cc; }
    public void setCc(List<String> cc) { this.cc = cc; }

    public List<String> getBcc() { return bcc; }
    public void setBcc(List<String> bcc) { this.bcc = bcc; }
}