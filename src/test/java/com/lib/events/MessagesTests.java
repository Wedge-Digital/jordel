package com.lib.events;


import com.lib.services.MessageSourceConfig;
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

        String msgInterpolated = messageSource.getMessage("user_registration.username.already_exists", new Object[]{"email_de_test"}, Locale.ENGLISH);
        assertThat(msgInterpolated).isEqualTo("Username email_de_test already exists, please pick another one");

        String msgInterpolated_fr = messageSource.getMessage("user_registration.username.already_exists", new Object[]{"email_de_test"}, Locale.FRENCH);
        assertThat(msgInterpolated_fr).isEqualTo("Le nom d'utilisateur email_de_test est déjà attribué, merci de choisir un autre");
    }


}
