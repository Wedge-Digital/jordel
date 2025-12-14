package com.bloodbowlclub.shared.coach;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(using = ValueObjectSerializer.class)
public class CoachID extends EntityID {
    public CoachID(String id) {
        super(id);
    }
}
