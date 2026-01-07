package com.bloodbowlclub.test_utilities;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.ResultMap;
import org.junit.jupiter.api.Assertions;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Locale;

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

    public static void assertResultContainsError(ResultMap<Void> result, String key, String expectedErrorMessage, MessageSource messageSource) {
        HashMap<String, String> expectedErrorMessages = new HashMap<>();
        expectedErrorMessages.put(
                key,
                expectedErrorMessage);
        Assertions.assertEquals(
                result.getTranslatedErrorMap(messageSource, Locale.getDefault()),
                expectedErrorMessages);
    }
}
