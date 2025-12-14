package com.bloodbowlclub.test_utilities;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import org.junit.jupiter.api.Assertions;

public class AssertLib {

    public static void AssertHasNoDomainEvent(AggregateRoot agg) {
        Assertions.assertEquals(0, agg.domainEvents().size());
    }

    public static void AssertHasDomainEvent(AggregateRoot agg) {
        Assertions.assertEquals(1, agg.domainEvents().size());
    }

    public static void AssertHasDomainEventOfType(AggregateRoot agg, Class klass) {
        AssertHasDomainEvent(agg);
        DomainEvent evt = agg.domainEvents().getFirst();
        Assertions.assertEquals(klass, evt.getClass());
    }
}
