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

@Service("aggregateShallExistPolicy")
public class AgregateShallExistPolicy extends Policy {

    public AgregateShallExistPolicy(@Qualifier("eventStore") EventStore eventStore) {
        super(eventStore);
    }

    public TranslatableMessage getErrorMsg(String userAccountId) {
        return new TranslatableMessage("user_account.not_existing", userAccountId);
    }

    public ResultMap<Void> check(String agregateId) {
        List<EventEntity> result = this.eventStore.findBySubject(agregateId);
        if (result.isEmpty()) {
            return ResultMap.failure("UserAccount", getErrorMsg(agregateId), ErrorCode.NOT_FOUND);
        }
        return ResultMap.success(null);
    }
}
