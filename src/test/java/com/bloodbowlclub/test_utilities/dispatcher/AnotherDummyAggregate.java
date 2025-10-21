package com.bloodbowlclub.test_utilities.dispatcher;

import com.bloodbowlclub.lib.domain.AggregateRoot;

public class AnotherDummyAggregate extends AggregateRoot {
    @Override
    public String getId() {
        return "tata";
    }
}
