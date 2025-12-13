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

@Service("aggregateShallExistPolicy")
public class AgregateShallExistPolicy extends Policy {

    public AgregateShallExistPolicy(MessageSource msgSource, @Qualifier("eventStore") EventStore eventStore) {
        super(msgSource, eventStore);
    }

    public String getErrorMsg(String userAccountId) {
        return msgSource.getMessage("user_account.not_existing", new Object[]{userAccountId}, Locale.getDefault());
    }

    public ResultMap<Void> check(String agregateId) {
        List<EventEntity> result = this.eventStore.findBySubject(agregateId);
        if (result.isEmpty()) {
            return ResultMap.failure("UserAccount", getErrorMsg(agregateId), ErrorCode.NOT_FOUND);
        }
        return ResultMap.success(null);
    }
}
