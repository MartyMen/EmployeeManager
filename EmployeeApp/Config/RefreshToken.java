package com.example.EmployeeApp.Config;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue
    private long id;
    private String token;
    private Instant expiryDate;


    public RefreshToken() {
    }

    public RefreshToken(long id, String token, Instant expiryDate, Instant createdDate) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
        this.createdDate = createdDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    private Instant createdDate;
}
