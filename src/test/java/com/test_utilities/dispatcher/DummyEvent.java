package com.test_utilities.dispatcher;

import com.lib.domain.events.DomainEvent;

public class DummyEvent extends DomainEvent {
    public DummyEvent() {
        super("source", "connectedUser");
    }

    public DummyEvent(String eventId) {
        super(eventId, "connectedUser");
    }
}
