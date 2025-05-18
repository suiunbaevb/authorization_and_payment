package com.example.payment_api.controller;

import com.example.payment_api.security.JwtUtil;
import com.example.payment_api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping ("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService payment;
    private final JwtUtil jwt;

    @PostMapping("/payment")
    public Map<String, BigDecimal> pay(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearer) {
        Long uid = jwt.getUserId(bearer.substring(7));
        BigDecimal balance = payment.pay(uid);
        return Map.of("balance", balance);
    }
}
