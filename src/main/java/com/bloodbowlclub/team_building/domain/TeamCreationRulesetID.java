package com.bloodbowlclub.team_building.domain;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class TeamCreationRulesetID extends EntityID {
    public TeamCreationRulesetID(String id) {
        super(id);
    }
}
