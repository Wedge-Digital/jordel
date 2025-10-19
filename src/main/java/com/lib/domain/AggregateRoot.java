package com.lib.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lib.domain.events.DomainEvent;
import com.lib.services.ResultMap;
import com.lib.validators.DomainValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@NoArgsConstructor
public abstract class AggregateRoot {

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

    public ResultMap<String> validationErrors(){
        HashMap<String, String> errorMap = (HashMap<String, String>) DomainValidator.getErrors(this);
        if(errorMap.isEmpty()){
            return ResultMap.success("");
        }
        return ResultMap.failure(errorMap);
    }
}
