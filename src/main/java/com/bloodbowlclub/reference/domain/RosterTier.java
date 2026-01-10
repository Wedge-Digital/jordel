package com.bloodbowlclub.reference.domain;

import com.bloodbowlclub.lib.domain.ValueObject;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RosterTier extends ValueObject {
    @NotBlank(message = "Roster tier cannot be blank")
    private final String value;

    public RosterTier(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String id) {
        return id.equals(value);
    }
}
