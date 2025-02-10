package com.paymybuddy.mvp.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.paymybuddy.mvp.errors.EErrorEmail;
import com.paymybuddy.mvp.model.internal.Email;

import org.junit.jupiter.api.Test;

class EmailTest {
    private final String invalidEmail = "0xDEADBEEF";
    private final String validEmail = "test@test.com";

    @Test
    void errorInvalid() {
        final var expected = new EErrorEmail().new Invalid(invalidEmail);
        final var actual = Email.builder().email(invalidEmail).tryBuild().err().get();

        assertEquals(expected, actual);
    }

    @Test
    void errorNull() {
        final var expected = new EErrorEmail().new Null();
        final var actual = Email.builder().tryBuild().err().get();

        assertEquals(expected, actual);
    }

    @Test
    void constructorError() {
        assertThrows(EErrorEmail.Invalid.class, () -> new Email(invalidEmail));
    }

    @Test
    void emailSuccess() {
        final var expected = validEmail;
        final var actual = Email.builder().email(validEmail).tryBuild().expect();

        assertEquals(expected, actual.getEmail());
    }

    @Test
    void constructorSuccess() {
        assertDoesNotThrow(() -> new Email(validEmail));
    }
}
