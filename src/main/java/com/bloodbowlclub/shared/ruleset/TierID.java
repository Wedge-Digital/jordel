package com.bloodbowlclub.shared.ruleset;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class TierID extends EntityID {
    public TierID(String id) {
        super(id);
    }
}
