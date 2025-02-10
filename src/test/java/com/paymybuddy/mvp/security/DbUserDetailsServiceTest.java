package com.paymybuddy.mvp.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DbUserDetailsServiceTest {
    private User userJoneDoe = HelperTest.johnDoe();

    @Test
    void loadUserByUsernameNull() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> dbUserDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsernameNotEmail() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> dbUserDetailsService.loadUserByUsername("0xDEADBEEF"));
    }

    @Test
    void loadUserByUsernameDoesNotExist() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> dbUserDetailsService.loadUserByUsername(
                        userJoneDoe.getEmail().toString()));
    }

    @Test
    void loadUserByUsernameSuccess() {
        userJoneDoe = userRepository.save(userJoneDoe);
        final var actualUserDetails =
                dbUserDetailsService.loadUserByUsername(userJoneDoe.getEmail().toString());

        assertEquals(userJoneDoe.getEmail().toString(), actualUserDetails.getUsername());
        assertEquals(userJoneDoe.getPassword().toString(), actualUserDetails.getPassword());
    }

    // -- Beans --

    @NonNull private final UserRepository userRepository;

    @NonNull private final  DbUserDetailsService dbUserDetailsService;

    @Autowired
    DbUserDetailsServiceTest(
            @NonNull final UserRepository userRepository,
            @NonNull final DbUserDetailsService dbUserDetailsService) {
        this.userRepository = userRepository;
        this.dbUserDetailsService = dbUserDetailsService;
    }
}
