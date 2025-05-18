package com.example.payment_api.repository;

import com.example.payment_api.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByValue(String value);
}
