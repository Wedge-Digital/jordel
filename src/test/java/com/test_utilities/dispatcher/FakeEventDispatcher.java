package com.test_utilities.dispatcher;

import com.shared.domain.events.AbstractEventDispatcher;
import com.shared.domain.events.DomainEvent;
import com.shared.domain.events.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class FakeEventDispatcher implements AbstractEventDispatcher {

    private List<DomainEvent> dispatchedEvents = new ArrayList<DomainEvent>();

    @Override
    public <E extends DomainEvent> void subscribe(Class<E> eventType, EventHandler subscriber) {
    }

    @Override
    public <E extends DomainEvent> void unsubscribe(Class<E> eventType, EventHandler subscriber) {
    }

    @Override
    public <E extends DomainEvent> void dispatch(E event) {
        dispatchedEvents.add(event);
    }

    @Override
    public <E extends DomainEvent> void dispatchAll(List<E> eventList) {
        eventList.forEach(this::dispatch);
    }

    public List<DomainEvent> getDispatchedEvents() {
        return dispatchedEvents;
    }
}
