package com.paymybuddy.mvp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Secret;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTests {
    private final User userJohnDoe = HelperTest.johnDoe();

    @Test
    void saveWithSpecificId() {
        userRepository.save(userJohnDoe);

        final var expected = userJohnDoe.getId();
        final var actual = userRepository.getReferenceById(expected).getId();
        assertEquals(expected, actual);
    }

    @Test
    void saveWithGenerateUuid() {
        final var userNoUuid = User.builder()
                .username("Username")
                .email(Email.builder().email("email@test.com").tryBuild().expect())
                .password(new Secret("password"))
                .build();

        userRepository.save(userNoUuid);

        final var expected = userNoUuid.getId();
        final var actual = userRepository.getReferenceById(expected).getId();
        assertEquals(expected, actual);
    }

    @Test
    void tryFindByIdExist() {
        userRepository.save(userJohnDoe);

        final var expected = userJohnDoe;
        final var actual = userRepository.tryFindById(userJohnDoe.getId()).expect();
        assertEquals(expected, actual);
    }

    @Test
    void tryFindByIdDoesNotExist() {
        final var expected = new EErrorCrud()
        .new Exist(userJohnDoe.getId(), new EErrorCrud.EExist.DoesNotExist());
        final var actual = userRepository.tryFindById(userJohnDoe.getId()).err().get();
        assertEquals(expected, actual);
    }

    // -- Beans --

    @NonNull private final UserRepository userRepository;

    @Autowired
    UserRepositoryTests(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
