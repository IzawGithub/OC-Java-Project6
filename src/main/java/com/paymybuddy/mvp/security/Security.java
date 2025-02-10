package com.paymybuddy.mvp.security;

import com.paymybuddy.mvp.model.internal.Secret;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class Security {
    @Bean
    @NonNull PasswordEncoder passwordEncoder() {
        return Secret.PASSWORD_ENCODER;
    }

    /**
     * @see
     *     https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#jc-httpsecurity
     */
    @Bean
    @NonNull SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                        authorization -> authorization.anyRequest().permitAll())
                .csrf(Customizer.withDefaults())
                .build();
    }
}
