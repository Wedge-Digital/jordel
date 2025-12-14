package com.bloodbowlclub.lib;

import com.bloodbowlclub.lib.services.email_service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class EmailClientTest {

    @Autowired
    private EmailService emailService;

    @Test
    @DisplayName("bare client")
    void test_bare_client() throws MessagingException {
//        emailService.sendRawHtmlMessage("bertrand.begouin@gmail.com", "hellooooo", "<html><body><h1>Hello {{name}}</h1><p>Your order {{username}} is {{var_1}}.</p></body></html>");
    }

    @Test
    @DisplayName("test send html client")
    void test_send_html_client() throws MessagingException {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("username", "Bagouze");
        variables.put("reset_pwd_url", "https://bloodbowlclub.com/reset_password?token=1234567890");
//        emailService.sendtemplatizedMessage("bertrand.begouin@gmail.com", variables);
    }

    @Test
    @DisplayName("test send lostLogin template")
    void test_send_lost_login_template() throws MessagingException {
//       emailService.sendResetPasswordEmail("bertrand.begouin@gmail.com", "Gouze", "https://bloodbowlclub.com/reset_password?token=1234567890");
    }

}
