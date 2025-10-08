package com.shared.domain.events;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class EventDispatcher implements AbstractEventDispatcher{

    // Map du type d'event vers liste des subscribers (handlers)
    private Map<Class<? extends DomainEvent>, List<EventHandler>> subscribers = new ConcurrentHashMap<>();
    private List<EventHandler> omniSuscribers = new ArrayList<>();

    // S'abonner à un type d'event
    public <E extends DomainEvent> void subscribe(Class<E> eventType, EventHandler subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    public void subscribe_all(EventHandler subscriber) {
        omniSuscribers.add(subscriber);
    }

    public <E extends DomainEvent> void unsubscribe(Class<E> eventType, EventHandler subscriber) {
        List<EventHandler> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers != null) {
            eventSubscribers.remove(subscriber);
        }
    }

    // Dispatcher un event instance à tous les subscribers du type correspondant
    @SuppressWarnings("unchecked")
    public <E extends DomainEvent> void dispatch(E event) {
        Thread.ofVirtual().start(() -> {
            synchronized (omniSuscribers) {
                omniSuscribers.forEach(subscriber ->
                        Thread.ofVirtual().start(() -> subscriber.receive(event))
                );
            }

            Class<? extends DomainEvent> eventClass = event.getClass();
            List<EventHandler> eventSubscribers = subscribers.get(eventClass);
            if (eventSubscribers == null) {
                return;
            }
            synchronized (eventSubscribers) {
                eventSubscribers.forEach(subscriber ->
                        Thread.ofVirtual().start(() -> subscriber.receive(event))
                );
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <E extends DomainEvent> void dispatchAll(List<E> eventList) {
        eventList.forEach(this::dispatch);
    }
}
