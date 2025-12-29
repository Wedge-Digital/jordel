package com.bloodbowlclub.lib.domain.events;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(com.bloodbowlclub.lib.io.serialization.resolvers.DomainEventTypeIdResolver.class)
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

    /**
     * Applique cet événement à un agrégat (premier dispatch du double dispatch)
     * @param aggregate l'agrégat sur lequel appliquer l'événement
     * @return le résultat de l'application (nouvel état de l'agrégat ou erreur)
     */
    public abstract Result<AggregateRoot> applyTo(AggregateRoot aggregate);

}