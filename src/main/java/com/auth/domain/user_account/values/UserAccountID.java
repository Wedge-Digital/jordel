package com.auth.domain.user_account.values;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shared.EntityID;
import com.shared.domain.serializers.ValueObjectSerializer;

@JsonSerialize(using = ValueObjectSerializer.class)
public class UserAccountID extends EntityID {
    public UserAccountID(String id) {
        super(id);
    }
}

