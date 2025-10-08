package com.test_utilities.dispatcher;

import com.shared.domain.events.DomainEvent;
import com.shared.domain.events.EventHandler;

public class DummyEventSuscriber implements EventHandler {
    private int handleCount = 0;

    @Override
    public void receive(DomainEvent event) {
        handleCount++;
    }

    public int getHandleCount() {
        return handleCount;
    }
}
