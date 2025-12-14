package com.bloodbowlclub.shared.team;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class TeamID extends EntityID {
    public TeamID(String id) {
        super(id);
    }
}
