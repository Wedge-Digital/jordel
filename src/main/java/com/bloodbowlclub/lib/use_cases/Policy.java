package com.bloodbowlclub.lib.use_cases;

import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.result.ResultMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


@Service
public abstract class Policy {

    protected final MessageSource msgSource;

    @Qualifier("eventStore")
    protected final EventStore eventStore;

    public Policy(MessageSource msgSource, @Qualifier("eventStore") EventStore eventStore) {
        this.msgSource = msgSource;
        this.eventStore = eventStore;
    }

    public abstract String getErrorMsg(String predicate);

    public abstract ResultMap<Void> check(String predicate);
}
