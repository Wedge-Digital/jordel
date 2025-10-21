package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.AggregateRoot;

public class DummyAggregate extends AggregateRoot {
    @Override
    public String getId() {
        return "toto";
    }
}
