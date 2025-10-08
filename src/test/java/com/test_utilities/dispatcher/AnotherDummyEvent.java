package com.test_utilities.dispatcher;

import com.shared.domain.events.DomainEvent;
import com.shared.domain.events.InvalidAggregateRoot;

public class AnotherDummyEvent extends DomainEvent {
    public AnotherDummyEvent() throws InvalidAggregateRoot {
        super("anotherSource");
    }
}
