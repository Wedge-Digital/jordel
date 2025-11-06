package com.bloodbowlclub.lib.tests;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

public class TestCase {

    protected MessageSource messageSource = messageSource();

    private ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("lang/messages", "lang/error", "ValidationMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
