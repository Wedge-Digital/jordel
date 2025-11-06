package com.bloodbowlclub.lib.persistance.read_cache;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
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

    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "TEXT", updatable = false)
    private AggregateRoot data;

    // data (String) : données JSON sérialisées (payload)

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Constructeurs
    public ReadEntity(AggregateRoot aggregateRoot) {
        this.id = aggregateRoot.getId();
        this.type = "";
        this.version = aggregateRoot.getVersion();
        this.data = aggregateRoot;
        this.createdAt = Instant.now();
    }

    public ReadEntity() {
    }

}
