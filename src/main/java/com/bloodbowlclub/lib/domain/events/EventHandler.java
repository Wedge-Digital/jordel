package com.bloodbowlclub.lib.domain.events;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public abstract class EventHandler {
    protected final AbstractEventDispatcher dispatcher;

    protected EventHandler(@Qualifier("eventDispatcher") AbstractEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public abstract void receive(DomainEvent event);


    public abstract void initSubscription();

}
