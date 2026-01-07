package com.bloodbowlclub.lib.services;

import java.util.Locale;

public interface ITranslator {
    String translate(TranslatableMessage message, Locale locale);
}
