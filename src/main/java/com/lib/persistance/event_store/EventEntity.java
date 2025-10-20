package com.lib.persistance.event_store;

import com.lib.domain.events.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ulid4j.Ulid;

import java.time.Instant;

@Entity
@Table(name = "event_log")
public class EventEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;
    // id (String) : identifiant unique de l’événement

    @Column(name = "source", nullable = false,updatable = false)
    private String source;
    // source (String) : URI source de l’événement

    @Column(name = "type", nullable = false, updatable = false)
    private String type;
    // type (String) : type de l’événement

    @Column(name = "spec_version", nullable = false, updatable = false)
    private String specVersion;
    // specVersion (String) : version de la spécification (ex: "1.0")

    @Column(name = "time", nullable = false, updatable = false)
    private Instant time;
    // time (Instant) : timestamp de l’événement

    @Column(name = "data_schema", nullable = false, updatable = false)
    private String dataSchema;
    // dataschema (String) : schéma de la donnée (optionnel)

    @Column(name = "data_content_type", updatable = false)
    private String dataContentType;
    // datacontenttype (String) : type MIME du contenu (ex: "application/json")

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "TEXT", updatable = false)
    private DomainEvent data;
    // data (String) : données JSON sérialisées (payload)

    @Column(name = "subject", updatable = false)
    private String subject;
    // subject (String) : sujet ou ressource cible (optionnel)

    // Constructeurs
    public EventEntity(DomainEvent event, String creatorId){
        Ulid ulid = new Ulid();
        this.id = ulid.create();
        this.specVersion = "1.0";
        this.source = creatorId;
        this.subject = event.getAggregateId();
        this.type = event.getClass().getTypeName();
        this.data = event;
        this.time = Instant.now();
    }

    public EventEntity() {
    }

    // Getters & setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getDataSchema() {
        return dataSchema;
    }

    public void setDataSchema(String dataSchema) {
        this.dataSchema = dataSchema;
    }

    public String getDataContentType() {
        return dataContentType;
    }

    public void setDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    public DomainEvent getData() {
        return data;
    }

    public void setData(DomainEvent data) {
        this.data = data;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
