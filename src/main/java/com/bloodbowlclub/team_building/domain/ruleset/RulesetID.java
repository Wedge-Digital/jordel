package com.bloodbowlclub.team_building.domain.ruleset;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class RulesetID extends EntityID {
    public RulesetID(String id) {
        super(id);
    }
}
