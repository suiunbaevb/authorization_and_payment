package com.example.payment_api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.payment_api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.ttl}") private Duration ttl;

    public String generate(User user) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(ttl)))
                .sign(Algorithm.HMAC256(secret));
    }
    public Long getUserId(String token) {
        return Long.parseLong(JWT.require(Algorithm.HMAC256(secret))
                .build().verify(token).getSubject());
    }
    public Instant getExpiry(String token) {
        return JWT.decode(token).getExpiresAt().toInstant();
    }
}

