package com.shared.domain.events;

public class InvalidAggregateRoot extends Exception{
    public InvalidAggregateRoot(String message) {
        super(message);
    }
}
