package com.bloodbowlclub.lib.domain.events;

public interface EventHandler {

    void receive(DomainEvent event);
}
