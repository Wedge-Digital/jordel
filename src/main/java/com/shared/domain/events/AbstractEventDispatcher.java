package com.shared.domain.events;

import java.util.List;
import java.util.function.Consumer;

public interface AbstractEventDispatcher {
    public <E extends DomainEvent> void subscribe(Class<E> eventType, EventHandler subscriber);

    public <E extends DomainEvent> void unsubscribe(Class<E> eventType, EventHandler subscriber);

    public <E extends DomainEvent> void dispatch(E event);

    public <E extends DomainEvent> void dispatchAll(List<E> eventList);
}
