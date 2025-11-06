package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.events.DomainEvent;

public class DummyEvent extends DomainEvent {
    public DummyEvent() {
        super("connectedUser");
    }

    public DummyEvent(String eventId) {
        super(eventId);
    }
}
