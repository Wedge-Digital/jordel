package com.bloodbowlclub.auth.domain.user_account.values;

import com.bloodbowlclub.lib.domain.EntityID;
import com.bloodbowlclub.lib.domain.serializers.ValueObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ValueObjectSerializer.class)
public class UserAccountID extends EntityID {
    public UserAccountID(String id) {
        super(id);
    }
}

