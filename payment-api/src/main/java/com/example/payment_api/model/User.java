package com.example.payment_api.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="app_user") @Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique=true,nullable=false) private String username;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal balance = new BigDecimal("8.00");
    private Integer failedAttempts = 0;
    private Instant lockedUntil;
    // ← это новое поле для ролей
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();
}