package com.bloodbowlclub.lib.domain;

import com.bloodbowlclub.lib.services.result.ErrorCode;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.validators.DomainValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(com.bloodbowlclub.lib.io.serialization.resolvers.EntityTypeIdResolver.class)
@NoArgsConstructor
@SuperBuilder
public abstract class Entity implements Validable {
    public abstract String getId();


}
