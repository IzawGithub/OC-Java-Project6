package com.paymybuddy.mvp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserPayMyBuddyServiceTest {
    public static final Email EMAIL_PAY_MY_BUDDY =
            Email.builder().email("user@paymybuddy.com").tryBuild().expect();

    @Test
    void constructUserDoesNotExist() {
        final var actual = new UserPayMyBuddyService(userRepository).getUser();
        final var expected = userRepository.findByEmail(EMAIL_PAY_MY_BUDDY).get();
        assertEquals(expected, actual);
    }

    // -- Beans --

    @NonNull private final UserRepository userRepository;

    @Autowired
    UserPayMyBuddyServiceTest(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
