package com.test_utilities.dispatcher;

import com.lib.domain.events.DomainEvent;

public class AnotherDummyEvent extends DomainEvent {
    public AnotherDummyEvent() {
        super("anotherSource", "createUserId");
    }
}
