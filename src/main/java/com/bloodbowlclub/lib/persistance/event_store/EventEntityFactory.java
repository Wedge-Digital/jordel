package com.bloodbowlclub.lib.persistance.event_store;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.domain.events.UserDomainEvent;

public class EventEntityFactory {

    public EventEntity build(UserDomainEvent event) {
        return new EventEntity(event, event.getCreatedBy().toString());
    }

    public EventEntity build(DomainEvent event) {
        return new EventEntity(event, "Anonymous");
    }
}
