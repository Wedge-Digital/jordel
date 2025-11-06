package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.Result;

public class AnotherDummyAggregate extends AggregateRoot {
    @Override
    public Result<AggregateRoot> apply(DomainEvent event) {
        return null;
    }

    @Override
    public String getId() {
        return "tata";
    }
}
