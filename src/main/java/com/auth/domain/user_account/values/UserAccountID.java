package com.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lib.domain.EntityID;
import com.lib.domain.serializers.ValueObjectSerializer;

@JsonSerialize(using = ValueObjectSerializer.class)
public class UserAccountID extends EntityID {
    public UserAccountID(String id) {
        super(id);
    }
}

