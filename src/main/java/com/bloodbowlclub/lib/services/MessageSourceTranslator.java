package com.bloodbowlclub.lib.services;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("messageSourceTranslator")
public class MessageSourceTranslator implements ITranslator {

    private final MessageSource messageSource;

    public MessageSourceTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String translate(TranslatableMessage message, Locale locale) {
        return messageSource.getMessage(
                message.getMessageKey(),
                message.getParams(),
                locale
        );
    }
}
