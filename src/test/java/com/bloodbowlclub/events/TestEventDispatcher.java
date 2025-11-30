package com.bloodbowlclub.events;

import com.bloodbowlclub.lib.domain.events.EventDispatcher;
import com.bloodbowlclub.test_utilities.dispatcher.AnotherDummyEvent;
import com.bloodbowlclub.test_utilities.dispatcher.DummyEvent;
import com.bloodbowlclub.test_utilities.dispatcher.DummyEventSubscriber;
import com.bloodbowlclub.test_utilities.dispatcher.DummyProjector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

public class TestEventDispatcher {

    private final EventDispatcher dispatcher = new EventDispatcher();
    private final DummyEventSubscriber subscriber = new DummyEventSubscriber(dispatcher);

    @Test
    void test_dispatch_event_without_any_registered_subscriber_shall_do_nothing() {
        dispatcher.dispatch(new DummyEvent());
        Assertions.assertEquals(0, subscriber.getHandleCount());
    }

    @Test
    void test_dispatch_event_with_a_single_registered_subscriber_shall_call_the_subscriber() throws InterruptedException {
        dispatcher.subscribe(DummyEvent.class, subscriber);
        dispatcher.dispatch(new DummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(1, subscriber.getHandleCount());
    }

    @Test
    void test_dispatch_event_after_unsubscribing_shall_not_call_the_subscriber() {
        dispatcher.subscribe(DummyEvent.class, subscriber);
        dispatcher.unsubscribe(DummyEvent.class, subscriber);
        dispatcher.dispatch(new DummyEvent());
        Assertions.assertEquals(0, subscriber.getHandleCount());
    }

    @Test
    void test_suscribe_to_all_event_shall_be_called_by_all_events() throws InterruptedException {
        dispatcher.subscribe_all(subscriber);
        dispatcher.dispatch(new DummyEvent());
        dispatcher.dispatch(new AnotherDummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(2, subscriber.getHandleCount());
    }

    @Test
    void test_a_subscriber_shall_not_be_called_by_an_event_not_registered_in_the_dispatcher(
    ) throws InterruptedException {
        dispatcher.subscribe(AnotherDummyEvent.class, subscriber);
        dispatcher.dispatch(new DummyEvent());
        dispatcher.dispatch(new AnotherDummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(1, subscriber.getHandleCount());
    }

    @Test
    void test_dispatchEventSequence_dispatch_event_in_order() throws InterruptedException {
        dispatcher.subscribe(DummyEvent.class, subscriber);
        DummyEvent event1 = new DummyEvent("event_#1");
        DummyEvent event2 = new DummyEvent("event_#2");
        dispatcher.dispatchList(List.of(event1, event2));
        Thread.sleep(200);
        Assertions.assertEquals(2, subscriber.getHandleCount());
        Assertions.assertEquals("event_#1", subscriber.getDispatchedEvents().getFirst().getAggregateId());
        Assertions.assertEquals("event_#2", subscriber.getDispatchedEvents().getLast().getAggregateId());
    }

    @Test
    void test_projector_shall_be_notified_after_all_avents_have_been_dispatched() throws InterruptedException {
        DummyProjector dummyProjector = new DummyProjector();
        dispatcher.subscribe(DummyEvent.class, dummyProjector);
        dispatcher.subscribe(DummyEvent.class, subscriber);
        DummyEvent event1 = new DummyEvent("event_#1");
        DummyEvent event2 = new DummyEvent("event_#2");
        dispatcher.dispatchList(List.of(event1, event2));
        Thread.sleep(200);

        Assertions.assertEquals(2, subscriber.getHandleCount());
        Instant event_1_dispatchedTime = subscriber.getEventDispatchedTime(event1);
        Instant event_2_dispatchedTime = subscriber.getEventDispatchedTime(event2);

        Instant event_1_projectedTime = dummyProjector.getEventDispatchedTime(event1);

        Assertions.assertTrue(event_1_dispatchedTime.isBefore(event_2_dispatchedTime));

        Assertions.assertTrue(event_2_dispatchedTime.isBefore(event_1_projectedTime));
    }

}
