package com.auth.services;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleTranslator implements MessageTranslator {
    private final ResourceBundle bundle;

    public ResourceBundleTranslator(String baseName, Locale locale) {
        this.bundle = ResourceBundle.getBundle(baseName, locale);
    }

    public ResourceBundleTranslator(String baseName) {
        this(baseName, Locale.getDefault());
    }

    @Override
    public String translate(String key, Object... args) {
        try {
            String message = bundle.getString(key);
            if (args.length > 0) {
                return MessageFormat.format(message, args);
            }
            return message;
        } catch (Exception e) {
            return "Message not found: " + key;
        }
    }

    @Override
    public String translate(String key, String defaultMessage, Object... args) {
        try {
            String message = bundle.getString(key);
            if (args.length > 0) {
                return MessageFormat.format(message, args);
            }
            return message;
        } catch (Exception e) {
            if (args.length > 0) {
                return MessageFormat.format(defaultMessage, args);
            }
            return defaultMessage;
        }
    }
}
