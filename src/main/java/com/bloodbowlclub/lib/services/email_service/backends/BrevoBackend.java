package com.bloodbowlclub.lib.services.email_service.backends;

import brevoModel.*;
import com.bloodbowlclub.lib.services.email_service.EmailBackend;
import com.bloodbowlclub.lib.services.email_service.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevoApi.TransactionalEmailsApi;

public class BrevoBackend implements EmailBackend {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private TransactionalEmailsApi apiInstance;

    @PostConstruct
    public void init() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setApiKey(brevoApiKey);
        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }

    @Override
    public void sendEmail(EmailRequest request) throws Exception {
        SendSmtpEmail email = new SendSmtpEmail();

        // Set sender
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName(senderName);
        email.setSender(sender);

        // Set recipient
        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(request.getRecipientEmail());
        to.setName(request.getRecipientName());
        email.setTo(Arrays.asList(to));

        // Set subject
        email.setSubject(request.getSubject());

        // Set CC if present
        if (request.getCc() != null && !request.getCc().isEmpty()) {
            List<SendSmtpEmailCc> ccList = request.getCc().stream()
                    .map(emailAddress -> new SendSmtpEmailCc().email(emailAddress))
                    .collect(Collectors.toList());
            email.setCc(ccList);
        }

        // Set BCC if present
        if (request.getBcc() != null && !request.getBcc().isEmpty()) {
            List<SendSmtpEmailBcc> bccList = request.getBcc().stream()
                    .map(emailAddress -> new SendSmtpEmailBcc().email(emailAddress))
                    .collect(Collectors.toList());
            email.setBcc(bccList);
        }

        // Set HTML content
        email.setHtmlContent("<html><body><h1>Hello " + request.getRecipientName() + "</h1></body></html>");

        try {
            apiInstance.sendTransacEmail(email);
        } catch (ApiException e) {
            throw new RuntimeException("Failed to send email via Brevo", e);
        }
    }

    @Override
    public String getName() {
        return "BrevoBackend";
    }
}
