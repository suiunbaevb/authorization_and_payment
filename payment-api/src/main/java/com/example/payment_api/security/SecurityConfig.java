package com.example.payment_api.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;   // ваш фильтр с проверкой токена

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST-API => state-less, поэтому CSRF можно выключить
                .csrf(csrf -> csrf.disable())

                // никаких HTTP-сессий — работаем по JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // правила доступа
                .authorizeHttpRequests(auth -> auth
                        // публичные
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        // logout — только для аутентифицированных
                        .requestMatchers("/api/auth/logout").authenticated()
                        // платёжные эндпоинты — тоже только после JWT
                        .requestMatchers("/api/payment/**").authenticated()
                        // всё остальное — тоже под защитой
                        .anyRequest().authenticated()
                )

                // подкладываем наш фильтр до стандартного UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** BCrypt — тот же, что использован в AuthService */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Нужен, если позже захотите использовать AuthenticationManager */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

