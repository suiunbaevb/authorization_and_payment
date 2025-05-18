package com.example.payment_api.security;

import com.example.payment_api.model.Token;
import com.example.payment_api.model.User;
import com.example.payment_api.repository.TokenRepository;
import com.example.payment_api.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;
    private final TokenRepository tokens;
    private final UserRepository users;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // 1) Проверяем JWT-подпись и срок
                if (!jwt.validate(token)) {
                    throw new JwtException("Invalid or expired JWT");
                }

                // 2) Проверяем в БД статус токена
                Token dbToken = tokens.findByValue(token)
                        .orElseThrow(() -> new JwtException("Token not found in DB"));
                if (dbToken.isRevoked() || dbToken.getExpiresAt().isBefore(Instant.now())) {
                    throw new JwtException("Token revoked or expired");
                }

                // 3) Загружаем пользователя и роли
                Long userId = jwt.getUserId(token);
                User user = users.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
                var authorities = user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 4) Ставим Authentication в SecurityContext
                var authToken = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (JwtException | UsernameNotFoundException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}
