package com.bloodbowlclub.lib.persistance.read_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.services.ObjectMapperService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "read_cache")
public class ReadEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;
    // id (String) : identifiant unique de l'agrégat lié

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "type", nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "TEXT", updatable = false)
    private String data;
    // data (String) : données JSON sérialisées (payload)

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Constructeurs
    public ReadEntity(ReadEntityType type, AggregateRoot aggregateRoot) {
        this.id = aggregateRoot.getId();
        this.type = type.name();
        this.version = aggregateRoot.getVersion();
        ObjectMapperService mapperService = new ObjectMapperService();
        ObjectMapper mapper = mapperService.getMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(aggregateRoot);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                throw new NullPointerException("The data object is null");
            } else {
                throw new RuntimeException("Error while serializing the data object");
            }
        }
        this.data = json;
        this.createdAt = Instant.now();
    }

    public ReadEntity() {

    }
}
