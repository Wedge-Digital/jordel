package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;

public class AnotherDummyEvent extends DomainEvent {
    public AnotherDummyEvent() {
        super( "createUserId");
    }

    @Override
    public Result<AggregateRoot> applyTo(AggregateRoot aggregate) {
        return aggregate.apply(this);
    }
}
