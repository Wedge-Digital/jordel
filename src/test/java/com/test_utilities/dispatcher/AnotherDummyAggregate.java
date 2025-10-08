package com.test_utilities.dispatcher;

import com.shared.domain.AggregateRoot;

public class AnotherDummyAggregate extends AggregateRoot {
    @Override
    public String getId() {
        return "tata";
    }
}
