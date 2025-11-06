package com.bloodbowlclub.lib.persistance.event_store;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import org.springframework.context.MessageSource;

public class EventEntityFactory {

    public static EventEntity UserEventEntity(DomainEvent event, String eventCreator) {
        return new EventEntity(event, eventCreator);
    }

    public static EventEntity AnonymousEventEntity(DomainEvent event) {
        return new EventEntity(event, "Anonymous");
    }
}
