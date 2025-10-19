package com.lib.domain.events;

public interface Projector {

    void receive(DomainEvent event);
}
