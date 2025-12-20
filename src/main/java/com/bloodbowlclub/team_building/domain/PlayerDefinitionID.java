package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class PlayerDefinitionID extends EntityID {
    public PlayerDefinitionID(String id) {
        super(id);
    }
}
