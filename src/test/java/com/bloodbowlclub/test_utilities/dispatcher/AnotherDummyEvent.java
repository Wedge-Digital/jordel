package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.events.DomainEvent;

public class AnotherDummyEvent extends DomainEvent {
    public AnotherDummyEvent() {
        super( "createUserId");
    }
}
