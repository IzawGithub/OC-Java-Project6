package com.paymybuddy.mvp.security;

import com.paymybuddy.mvp.model.internal.Secret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class Security {
    private AuthenticationManager authManager;

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
        final var filterChain = http.authorizeHttpRequests(
                        authorization -> authorization.anyRequest().permitAll())
                .formLogin(login -> login.loginPage("/auth/log-in")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/auth/log-out")
                        .logoutSuccessUrl("/")
                        .addLogoutHandler(new HeaderWriterLogoutHandler(
                                new ClearSiteDataHeaderWriter(Directive.ALL)))
                        .permitAll())
                .csrf(Customizer.withDefaults())
                .rememberMe(Customizer.withDefaults())
                .build();

        final var authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManager = authManagerBuilder.getOrBuild();

        return filterChain;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false);
    }

    @Bean
    @DependsOn("filterChain")
    AuthenticationManager getAuthenticationManager() {
        return authManager;
    }
}
