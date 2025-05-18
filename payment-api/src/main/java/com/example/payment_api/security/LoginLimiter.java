package com.example.payment_api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class LoginLimiter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    public Bucket resolve(String username) {
        return buckets.computeIfAbsent(username, k ->
                Bucket4j.builder()
                        .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(15))))
                        .build());
    }
}

