package com.shared.domain.events;

import com.test_utilities.dispatcher.AnotherDummyEvent;
import com.test_utilities.dispatcher.DummyEvent;
import com.test_utilities.dispatcher.DummyEventSuscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestEventDispatcher {

    private final EventDispatcher dispatcher = new EventDispatcher();

    @Test
    void test_dispatch_event_without_any_registered_subscriber_shall_do_nothing() throws InvalidAggregateRoot {
        DummyEventSuscriber suscriber = new DummyEventSuscriber();
        dispatcher.dispatch(new DummyEvent());
        Assertions.assertEquals(0, suscriber.getHandleCount());
    }

    @Test
    void test_dispatch_event_with_a_single_registered_subscriber_shall_call_the_suscriber() throws InterruptedException, InvalidAggregateRoot {
        DummyEventSuscriber suscriber = new DummyEventSuscriber();
        dispatcher.subscribe(DummyEvent.class, suscriber);
        dispatcher.dispatch(new DummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(1, suscriber.getHandleCount());
    }

    @Test
    void test_dispatch_event_after_unsubscribing_shall_not_call_the_suscriber() throws InvalidAggregateRoot {
        DummyEventSuscriber suscriber = new DummyEventSuscriber();
        dispatcher.subscribe(DummyEvent.class, suscriber);
        dispatcher.unsubscribe(DummyEvent.class, suscriber);
        dispatcher.dispatch(new DummyEvent());
        Assertions.assertEquals(0, suscriber.getHandleCount());
    }

    @Test
    void test_suscribe_to_all_event_shall_be_called_by_all_events() throws InterruptedException, InvalidAggregateRoot {
        DummyEventSuscriber suscriber = new DummyEventSuscriber();
        dispatcher.subscribe_all(suscriber);
        dispatcher.dispatch(new DummyEvent());
        dispatcher.dispatch(new AnotherDummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(2, suscriber.getHandleCount());
    }

    @Test
    void test_a_suscriber_shall_not_be_called_by_an_event_not_registered_in_the_dispatcher(
    ) throws InterruptedException, InvalidAggregateRoot {
        DummyEventSuscriber suscriber = new DummyEventSuscriber();
        dispatcher.subscribe(AnotherDummyEvent.class, suscriber);
        dispatcher.dispatch(new DummyEvent());
        dispatcher.dispatch(new AnotherDummyEvent());
        Thread.sleep(200);
        Assertions.assertEquals(1, suscriber.getHandleCount());
    }

}
