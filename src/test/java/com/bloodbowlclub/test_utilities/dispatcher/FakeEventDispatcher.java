package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.domain.events.EventHandler;
import com.bloodbowlclub.lib.domain.events.Projector;

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
    public <E extends DomainEvent> void subscribe_all(EventHandler subscriber) {

    }

    @Override
    public <E extends DomainEvent> void subscribe(Class<E> eventType, Projector projector) {

    }

    @Override
    public <E extends DomainEvent> void dispatch(E event) {
        dispatchedEvents.add(event);
    }

    @Override
    public <E extends DomainEvent> void asyncDispatchList(List<E> eventList) {
        eventList.forEach(this::dispatch);
    }

    @Override
    public <E extends DomainEvent> void dispatchList(List<E> eventList) {
        eventList.forEach(this::dispatch);
    }

    public List<DomainEvent> getDispatchedEvents() {
        return dispatchedEvents;
    }
}
