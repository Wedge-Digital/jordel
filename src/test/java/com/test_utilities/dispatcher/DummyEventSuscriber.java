package com.test_utilities.dispatcher;

import com.lib.domain.events.DomainEvent;
import com.lib.domain.events.EventHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyEventSuscriber implements EventHandler {
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

    public List<DomainEvent> getDispatchedEvents() {
        return dispatchedEventsHistory.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }
}
