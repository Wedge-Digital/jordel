package com.test_utilities.dispatcher;

import com.lib.domain.AggregateRoot;

public class AnotherDummyAggregate extends AggregateRoot {
    @Override
    public String getId() {
        return "tata";
    }
}
