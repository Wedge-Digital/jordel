package com.bloodbowlclub.team_building.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import  com.bloodbowlclub.lib.domain.Entity;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
@SuperBuilder
@Getter
public class PlayerDefinition extends Entity {
    @Valid
    @NotNull
    @JsonIgnore
    PlayerDefinitionID playerDefinitionID;

    @Valid
    @NotNull
    PlayerDefinitionName name;

    @Valid
    @NotNull
    PlayerMaxQuantity maxQuantity;

    @Valid
    @NotNull
    PlayerPrice price;


    @Override
    public String getId() {
        return playerDefinitionID.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerDefinition def = (PlayerDefinition) o;
        return this.playerDefinitionID.equals(def.playerDefinitionID);
    }
}
