package com.lib.domain.events;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventDispatcher implements AbstractEventDispatcher{

    // Map du type d'event vers liste des subscribers (handlers)
    private Map<Class<? extends DomainEvent>, List<EventHandler>> subscribers = new ConcurrentHashMap<>();
    private List<EventHandler> omniSuscribers = new ArrayList<>();

    private Map<Class<? extends DomainEvent>, List<Projector>> projectors = new ConcurrentHashMap<>();

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

    @Override
    public <E extends DomainEvent> void subscribe(Class<E> eventType, Projector projector) {
        projectors.computeIfAbsent(eventType, k -> new ArrayList<>()).add(projector);
    }

    // Dispatcher un event instance à tous les subscribers du type correspondant
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

            List<Projector> projectorList = projectors.get(eventClass);
            if (projectorList == null) {
                return;
            }
            synchronized (projectorList) {
                projectorList.forEach( projector ->
                        Thread.ofVirtual().start(() -> projector.receive(event))
                        );
            }
        });
    }

    public <E extends DomainEvent> void asyncDispatchList(List<E> eventList) {
        eventList.forEach(this::dispatch);
    }

    public <E extends DomainEvent> void syncDispatch(E event) {
            omniSuscribers.forEach(subscriber ->
                    subscriber.receive(event)
            );

            Class<? extends DomainEvent> eventClass = event.getClass();
            List<EventHandler> eventSubscribers = subscribers.get(eventClass);
            if (eventSubscribers == null) {
                return;
            }
            eventSubscribers.forEach(subscriber -> subscriber.receive(event));
        }


    public <E extends DomainEvent> void syncDispatchToProjectors(E event) {
        Class<? extends DomainEvent> eventClass = event.getClass();
        List<Projector> projectorList = projectors.get(eventClass);
        if (projectorList == null) {
            return;
        }
        projectorList.forEach( projector -> projector.receive(event));
    }

    public <E extends DomainEvent> void dispatchList(List<E> eventList) {
        eventList.forEach(this::syncDispatch);
        eventList.forEach(this::syncDispatchToProjectors);
    }
}
