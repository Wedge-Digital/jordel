package com.bloodbowlclub.team_building.domain.team_staff;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class StaffID extends EntityID {
    public StaffID(String id) {
        super(id);
    }
}
