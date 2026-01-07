package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.TranslatableMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public abstract class Policy {

    @Qualifier("eventStore")
    protected final EventStore eventStore;

    public Policy(@Qualifier("eventStore") EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public abstract TranslatableMessage getErrorMsg(String predicate);

    public abstract ResultMap<Void> check(String predicate);
}
