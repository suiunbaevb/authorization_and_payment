//package com.example.payment_api.service;
//
//import com.example.payment_api.exception.TooManyAttemptsException;
//import com.example.payment_api.model.User;
//import com.example.payment_api.repository.TokenRepository;
//import com.example.payment_api.repository.UserRepository;
//import com.example.payment_api.security.JwtUtil;
//import com.example.payment_api.security.LoginLimiter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.BadCredentialsException;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock UserRepository users;
//    @Mock TokenRepository tokens;
//    @Mock JwtUtil jwt;
//    @Mock LoginLimiter limiter;
//    @InjectMocks AuthService auth;
//
//    private final String USERNAME = "bob";
//    private final String RAW = "pass";
//    private final String HASH = "$2a$10$abcdefghijklmnopqrstuv";
//
//    @BeforeEach
//    void setup() {
//        // сделал BCryptEncoder внутри, но можно замокать User с готовым hash
//        User u = new User();
//        u.setId(42L);
//        u.setUsername(USERNAME);
//        u.setPasswordHash(HASH);
//        u.setLockedUntil(null);
//        when(users.findByUsername(USERNAME)).thenReturn(Optional.of(u));
//        // ограничитель всегда позволяет
//        var bucket = Mockito.mock(io.github.bucket4j.Bucket.class);
//        when(bucket.tryConsume(1)).thenReturn(true);
//        when(limiter.resolve(USERNAME)).thenReturn(bucket);
//    }
//
//    @Test
//    void login_badPassword_throwsAndIncrementsAttempts() {
//        // пароль не совпадёт — should throw BadCredentialsException
//        assertThatThrownBy(() -> auth.login(USERNAME, "wrong"))
//                .isInstanceOf(BadCredentialsException.class);
//        // и сохранит user с увеличенным счётчиком
//        verify(users).save(argThat(u -> u.getFailedAttempts() == 1));
//    }
//
//    @Test
//    void login_tooManyAttempts_throwsTooManyAttempts() {
//        var bucket = limiter.resolve(USERNAME);
//        when(bucket.tryConsume(1)).thenReturn(false);
//        assertThatThrownBy(() -> auth.login(USERNAME, RAW))
//                .isInstanceOf(TooManyAttemptsException.class);
//    }
//}
//
