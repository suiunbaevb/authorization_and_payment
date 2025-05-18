package com.example.payment_api.service;

import com.example.payment_api.exception.TooManyAttemptsException;
import com.example.payment_api.model.Token;
import com.example.payment_api.model.User;
import com.example.payment_api.repository.TokenRepository;
import com.example.payment_api.repository.UserRepository;
import com.example.payment_api.security.JwtUtil;
import com.example.payment_api.security.LoginLimiter;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final TokenRepository tokens;
    private final JwtUtil jwt;
    private final LoginLimiter limiter;
    private final PasswordEncoder encoder;

    public String login(String username, String rawPassword) {
        Bucket bucket = limiter.resolve(username);
        if (!bucket.tryConsume(1)) {
            throw new TooManyAttemptsException();
        }

        User user = users.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new LockedException("Account locked until " + user.getLockedUntil());
        }

        if (!encoder.matches(rawPassword, user.getPasswordHash())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setLockedUntil(Instant.now().plus(Duration.ofMinutes(15)));
            }
            users.save(user);
            throw new BadCredentialsException("Wrong password");
        }

        // успешный логин
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        users.save(user);

        String tokenValue = jwt.generate(user);
        Token token = new Token();
        token.setValue(tokenValue);
        token.setExpiresAt(jwt.getExpiry(tokenValue));
        token.setUser(user);
        tokens.save(token);

        return tokenValue;
    }

    public String register(String username, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new BadCredentialsException("Username '" + username + "' is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setRoles(List.of("ROLE_USER"));
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        users.save(user);

        // — по желанию — сразу возвращаем JWT после регистрации:
        String tokenValue = jwt.generate(user);
        Token token = new Token();
        token.setValue(tokenValue);
        token.setExpiresAt(jwt.getExpiry(tokenValue));
        token.setUser(user);
        tokens.save(token);

        return tokenValue;
    }

    public void logout(String token) {
        tokens.findByValue(token)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    tokens.save(t);
                });
    }
}
