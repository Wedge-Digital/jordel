package com.auth.db_model;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "access__account")
public class User {

    @Id
    @Column(name = "entity_id", unique = true, nullable = false)
    private String id;

    @Column(name = "coach_name", unique = true, nullable = false)
    private String coachName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String password;

    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "last_login")
    private Date lastLogin;

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Getters and setters

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCoachName() {
        return coachName;
    }
    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
