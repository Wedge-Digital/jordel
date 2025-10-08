package com.shared.domain.events;

public interface EventHandler {

    void receive(DomainEvent event);
}
