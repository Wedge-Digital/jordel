package com.test_utilities.dispatcher;

import com.lib.domain.events.DomainEvent;
import com.lib.domain.events.EventHandler;
import com.lib.domain.events.Projector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class DummyProjector implements Projector {

    private int handleCount = 0;
    private final HashMap<DomainEvent, Instant> dispatchedEventsHistory = new HashMap<>();


    @Override
    public void receive(DomainEvent event) {
        dispatchedEventsHistory.put(event, Instant.now());
        handleCount++;
    }

    public Instant getEventDispatchedTime(DomainEvent event) {
        return dispatchedEventsHistory.get(event);
    }

    public int getHandleCount() {
        return handleCount;
    }

    public ArrayList<DomainEvent> getDispatchedEvents() {
        return dispatchedEventsHistory.keySet().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
