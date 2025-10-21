package com.bloodbowlclub.lib.domain.events;

import java.util.List;

public interface AbstractEventDispatcher {
    public <E extends DomainEvent> void subscribe(Class<E> eventType, EventHandler subscriber);

    public <E extends DomainEvent> void unsubscribe(Class<E> eventType, EventHandler subscriber);

    public <E extends DomainEvent> void subscribe_all(EventHandler subscriber);

    public <E extends DomainEvent> void subscribe(Class<E> eventType, Projector projector);

    public <E extends DomainEvent> void dispatch(E event);

    public <E extends DomainEvent> void asyncDispatchList(List<E> eventList);

    public <E extends DomainEvent> void dispatchList(List<E> eventList);
}
