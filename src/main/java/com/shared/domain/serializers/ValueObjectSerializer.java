package com.shared.domain.serializers;

import com.auth.domain.user_account.values.UserAccountID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.shared.ValueObject;

import java.io.IOException;

public class ValueObjectSerializer extends JsonSerializer<ValueObject> {
    @Override
    public void serialize(ValueObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}