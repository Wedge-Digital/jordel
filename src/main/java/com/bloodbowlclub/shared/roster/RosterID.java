package com.bloodbowlclub.shared.roster;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class RosterID extends EntityID {
    public RosterID(String id) {
        super(id);
    }
}
