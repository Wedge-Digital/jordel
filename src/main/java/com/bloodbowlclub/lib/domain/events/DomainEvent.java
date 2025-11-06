package com.bloodbowlclub.lib.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@SuperBuilder
public abstract class DomainEvent {
    private final String aggregateId;

    private String createdBy;

    private Instant timeStampedAt;

    public DomainEvent() {
        this.aggregateId = null;
    }

    public DomainEvent(String agregateId, String createdBy) {
        this.aggregateId = agregateId;
        this.createdBy = createdBy;
        this.timeStampedAt = Instant.now();
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Instant getTimeStampedAt() {
        return timeStampedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

}