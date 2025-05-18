package com.example.payment_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal amount;
    private Instant createdAt = Instant.now();
    @ManyToOne(fetch= FetchType.LAZY) private User user;
}