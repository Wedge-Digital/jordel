package com.bloodbowlclub.shared.skills;

import com.bloodbowlclub.lib.domain.ValueObject;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@JsonSerialize(using = ValueObjectSerializer.class)
public class SkillID extends ValueObject<String> {

    @NotEmpty(message = "{skillID.notEmpty}")
    @Pattern(regexp = "^[A-Z][A-Z0-9_ ]*$", message = "{rosterID.invalid}")
    private final String value;

    public SkillID(String id) {
        this.value = id;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equalsString(String id) {
        return this.value.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.bloodbowlclub.shared.inducement.InducementID skillID = (com.bloodbowlclub.shared.inducement.InducementID) o;
        return value.equals(skillID.toString());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

