package com.bloodbowlclub.auth.use_cases.policies;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("aggregateShallNotExistPolicy")
public class AgregateShallNotExistPolicy extends Policy {


    public AgregateShallNotExistPolicy(@Qualifier("eventStore") EventStore eventStore) {
        super(eventStore);
    }

    public TranslatableMessage getErrorMsg(String username) {
        return new TranslatableMessage("user_registration.username.already_exists", username);
    }

    public ResultMap<Void> check(String userId) {
        List<EventEntity> result = this.eventStore.findBySubject(userId);
        if (result.isEmpty()) {
            return ResultMap.success(null);
        }
        return ResultMap.failure("aggregateId", getErrorMsg(userId), ErrorCode.ALREADY_EXISTS);
    }
}
