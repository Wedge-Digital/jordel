package com.bloodbowlclub.auth.io.repositories;

import com.bloodbowlclub.lib.domain.events.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ulid4j.Ulid;

import java.time.Instant;

@Entity
@Table(name = "lost_login_token")
@Data
@NoArgsConstructor
public class LostLoginTokenEntity {

    @Id
    @Column(name = "token", nullable = false,updatable = false)
    private String token;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "time", nullable = false, updatable = false)
    private Instant time;

    // Constructeurs
    public LostLoginTokenEntity(String username){
        Ulid ulid = new Ulid();
        this.token = ulid.create();
        this.username = username;
        this.time = Instant.now();
    }
}
