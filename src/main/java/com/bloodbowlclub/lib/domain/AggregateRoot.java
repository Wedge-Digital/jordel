package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(com.bloodbowlclub.lib.io.serialization.resolvers.AggregateRootTypeIdResolver.class)
@NoArgsConstructor
@SuperBuilder
public abstract class AggregateRoot extends DomainEventApplier implements Validable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AggregateRoot.class);

    @Getter
    protected int version;

    @JsonIgnore
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void addEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public abstract String getId();

    public Result<AggregateRoot> reconstruct(List<DomainEvent> domainEvents) {
        this.domainEvents.clear();
        domainEvents.forEach(this::apply);
        this.domainEvents.addAll(domainEvents);
        return Result.success(this);
    }

    /**
     * Point d'entrée pour l'application d'un événement (double dispatch)
     * Délègue à l'événement qui appellera la méthode apply typée appropriée
     */
    public Result<AggregateRoot> apply(DomainEvent event) {
        return event.applyTo(this);
    }


    public Result<AggregateRoot> hydrate(List<DomainEvent> eventList) {
        // l'hydratation doit se faire en ordre chronologique
        List<DomainEvent> chronologicalEvents = eventList.stream()
                .sorted(Comparator.comparing(DomainEvent::getTimeStampedAt))
                .toList();
        AggregateRoot root = this;
        for (DomainEvent event : chronologicalEvents) {
            Result<AggregateRoot> currentApplication = root.apply(event);
            if (currentApplication.isFailure()) {
                return currentApplication;
            }
            root = currentApplication.getValue();
        }
        return Result.success(root);
    }
}
