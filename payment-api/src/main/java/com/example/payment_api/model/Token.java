package com.example.payment_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    private Instant expiresAt;
    private boolean revoked;
    @ManyToOne(fetch= FetchType.LAZY) private User user;
}