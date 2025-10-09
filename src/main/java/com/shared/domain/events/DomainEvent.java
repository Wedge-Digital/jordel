package com.shared.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public abstract class DomainEvent {
    private String aggregateId;
    private Instant timeStampedAt;

    public DomainEvent() {
        this.aggregateId = null;
    }

    public DomainEvent(String source) {
        this.aggregateId = source;
        this.timeStampedAt = Instant.now();
    }

    public String getAggregateId() {
        return aggregateId;
    }
    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
     }

    public Instant getTimeStampedAt() {
        return timeStampedAt;
    }

}