package com.bloodbowlclub.shared.shared.cloudinary_url;

import com.bloodbowlclub.lib.domain.ValueObject;

public class CloudinaryUrl extends ValueObject<String> {

    @CloudinaryUrlConstraint()
    String value;

    public CloudinaryUrl(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equalsString(String id) {
        return false;
    }
}
