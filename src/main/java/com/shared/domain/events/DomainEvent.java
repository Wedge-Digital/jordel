package com.shared.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public abstract class DomainEvent {
    private String aggregateId;

    public DomainEvent() {
        this.aggregateId = null;
    }

    public DomainEvent(String source) {
        this.aggregateId = source;
    }

    public String getAggregateId() {
        return aggregateId;
    }
     public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
     }
}