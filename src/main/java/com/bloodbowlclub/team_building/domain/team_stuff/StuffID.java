package com.bloodbowlclub.team_building.domain.team_stuff;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class StuffID extends EntityID {
    public StuffID(String id) {
        super(id);
    }
}
