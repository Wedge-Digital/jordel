package com.bloodbowlclub.auth.use_cases.policies;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.Policy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service("aggregateShallNotExistPolicy")
public class AgregateShallNotExistPolicy extends Policy {


    public AgregateShallNotExistPolicy(MessageSource msgSource, @Qualifier("EventStore") EventStore eventStore) {
        super(msgSource, eventStore);
    }

    public String getErrorMsg(String username) {
        return msgSource.getMessage("user_registration.username.already_exists", new Object[]{username}, Locale.getDefault());
    }

    public ResultMap<Void> check(String userId) {
        List<EventEntity> result = this.eventStore.findBySubject(userId);
        if (result.isEmpty()) {
            return ResultMap.success(null);
        }
        return ResultMap.failure("aggregateId", getErrorMsg(userId), ErrorCode.ALREADY_EXISTS);
    }
}
