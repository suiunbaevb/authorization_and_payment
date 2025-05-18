package com.example.payment_api.service;


import com.example.payment_api.exception.InsufficientFundsException;
import com.example.payment_api.model.Payment;
import com.example.payment_api.model.User;
import com.example.payment_api.repository.PaymentRepository;
import com.example.payment_api.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository users;
    private final PaymentRepository payments;
    @Value("${payment.charge}") private BigDecimal charge;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal pay(Long userId) {
        User user = users.lockById(userId).orElseThrow();
        if (user.getBalance().compareTo(charge) < 0) throw new InsufficientFundsException();
        user.setBalance(user.getBalance().subtract(charge));
        Payment p = new Payment(); p.setAmount(charge); p.setUser(user);
        payments.save(p);
        // user automatically updated thanks to JPA dirty checking
        return user.getBalance();
    }
}



