package com.example.payment_api.repository;

import com.example.payment_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByUserId(Long userId);
}
