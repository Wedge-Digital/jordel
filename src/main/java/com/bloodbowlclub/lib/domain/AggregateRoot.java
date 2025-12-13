package com.bloodbowlclub.lib.domain;
import com.bloodbowlclub.auth.domain.user_account.events.*;
import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.Result;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.validators.DomainValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
@SuperBuilder
public abstract class AggregateRoot extends DomainEventApplier {

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

    @JsonIgnore
    public boolean isNotValid(){
        return !DomainValidator.isValid(this);
    }

    @JsonIgnore
    public boolean isValid(){
        return !isNotValid();
    }

    public abstract String getId();

    public ResultMap<Void> validationErrors(){
        HashMap<String, String> errorMap = (HashMap<String, String>) DomainValidator.getErrors(this);
        if(errorMap.isEmpty()){
            return ResultMap.success(null);
        }
        return ResultMap.failure(errorMap, ErrorCode.UNPROCESSABLE_ENTITY);
    }

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
