package com.bloodbowlclub.lib.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Data
public abstract class DomainEvent {
    private final String aggregateId;

    private Instant timeStampedAt;

    public DomainEvent() {
        this.aggregateId = null;
    }

    public DomainEvent(String agregateId) {
        this.aggregateId = agregateId;
        this.timeStampedAt = Instant.now();
    }

}