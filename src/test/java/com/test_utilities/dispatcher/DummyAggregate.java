package com.test_utilities.dispatcher;

import com.shared.domain.AggregateRoot;

public class DummyAggregate extends AggregateRoot {
    @Override
    public String getId() {
        return "toto";
    }
}
