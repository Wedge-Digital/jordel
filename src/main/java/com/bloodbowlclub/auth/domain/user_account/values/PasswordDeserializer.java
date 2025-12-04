package com.bloodbowlclub.auth.domain.user_account.values;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PasswordDeserializer extends JsonDeserializer<Password> {

    @Override
    public Password deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String encodedValue = p.getValueAsString();
        return new Password(encodedValue, false);
    }
}
