//package com.example.payment_api.service;
//
//import com.example.payment_api.exception.InsufficientFundsException;
//import com.example.payment_api.model.User;
//import com.example.payment_api.repository.PaymentRepository;
//import com.example.payment_api.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceTest {
//
//    @Mock UserRepository users;
//    @Mock PaymentRepository payments;
//    @InjectMocks PaymentService service;
//
//    @Test
//    void pay_decrementsBalanceAndSavesPayment() {
//        // given
//        User u = new User();
//        u.setId(1L);
//        u.setBalance(new BigDecimal("5.50"));
//        when(users.lockById(1L)).thenReturn(Optional.of(u));
//        // when
//        BigDecimal remaining = service.pay(1L);
//        // then
//        assertThat(remaining).isEqualByComparingTo("4.40"); // 5.50 − 1.10 = 4.40
//        ArgumentCaptor<com.example.payment_api.model.Payment> cap = ArgumentCaptor.forClass(com.example.payment_api.model.Payment.class);
//        verify(payments).save(cap.capture());
//        assertThat(cap.getValue().getAmount()).isEqualByComparingTo("1.10");
//    }
//
//    @Test
//    void pay_throwsWhenInsufficientFunds() {
//        User u = new User();
//        u.setId(2L);
//        u.setBalance(new BigDecimal("1.00"));
//        when(users.lockById(2L)).thenReturn(Optional.of(u));
//        assertThatThrownBy(() -> service.pay(2L))
//                .isInstanceOf(InsufficientFundsException.class);
//        verify(payments, never()).save(any());
//    }
//}
