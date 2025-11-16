package com.bloodbowlclub.lib.services.email_service.backends;

import com.bloodbowlclub.lib.services.email_service.EmailBackend;
import com.bloodbowlclub.lib.services.email_service.EmailRequest;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

public class ConsoleBackend implements EmailBackend {

    @Override
    public void sendEmail(EmailRequest request) throws Exception {
        System.out.println("=== EMAIL SENT TO CONSOLE ===");
        System.out.println("Recipient: " + request.getRecipientName() + " <" + request.getRecipientEmail() + ">");
        System.out.println("Subject: " + request.getSubject());
        System.out.println("Template: " + request.getTemplateName());
        System.out.println("Sent at: " + LocalDateTime.now());
        System.out.println("===============================");
    }

    @Override
    public String getName() {
        return "ConsoleBackend";
    }
}
