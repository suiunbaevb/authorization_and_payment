package com.example.payment_api.controller;

import com.example.payment_api.dto.AuthRequest;
import com.example.payment_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody AuthRequest r) {
        return Map.of("token", auth.login(r.username(), r.password()));
    }

    // — вот добавляем регистрацию:
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest r) {
        auth.register(r.username(), r.password());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User " + r.username() + " registered");
    }


    @PostMapping("/logout")
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearer) {
        String token = bearer.substring(7);
        auth.logout(token);
    }
}




