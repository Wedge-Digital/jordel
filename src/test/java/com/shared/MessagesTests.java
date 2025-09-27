package com.shared;


import com.shared.services.MessageSourceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessageSourceConfig.class)
public class MessagesTests {

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testMessageSource() {
        // Récupérer un message avec clé "welcome.message" en français
        String msg = messageSource.getMessage("welcome.title", null, Locale.FRENCH);

        // Vérifier le contenu du message (exemple attendu)
        assertThat(msg).isEqualTo("Bienvenue dans l'application");

        // Tester autre locale
        String msgEn = messageSource.getMessage("welcome.title", null, Locale.ENGLISH);
        assertThat(msgEn).isEqualTo("Welcome to the application");
    }


}
