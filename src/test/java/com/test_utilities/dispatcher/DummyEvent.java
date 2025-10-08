package com.test_utilities.dispatcher;

import com.shared.domain.events.DomainEvent;
import com.shared.domain.events.InvalidAggregateRoot;

public class DummyEvent extends DomainEvent {

    public DummyEvent() throws InvalidAggregateRoot {
        super("source");
    }
}
