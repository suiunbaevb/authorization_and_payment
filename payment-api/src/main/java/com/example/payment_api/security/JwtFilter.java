package com.example.payment_api.security;

import com.example.payment_api.model.User;
import com.example.payment_api.repository.TokenRepository;
import com.example.payment_api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;
    private final TokenRepository tokens;
    private final UserRepository users;

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth!=null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            boolean active = tokens.findByValue(token)
                    .filter(t -> !t.isRevoked() && t.getExpiresAt().isAfter(Instant.now()))
                    .isPresent();
            if (active) {
                Long userId = jwt.getUserId(token);
                User user = users.findById(userId).orElseThrow();
                UsernamePasswordAuthenticationToken authTok =
                        new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authTok);
            }
        }
        chain.doFilter(req,res);
    }
}

