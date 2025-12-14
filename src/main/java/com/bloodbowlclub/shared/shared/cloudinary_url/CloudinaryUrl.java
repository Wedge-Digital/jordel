package com.bloodbowlclub.shared.shared.cloudinary_url;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(using = ValueObjectSerializer.class)
public class CloudinaryUrl extends ValueObject<String> {

    @CloudinaryUrlConstraint()
    String value;

    public CloudinaryUrl(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String id) {
        return false;
    }
}
