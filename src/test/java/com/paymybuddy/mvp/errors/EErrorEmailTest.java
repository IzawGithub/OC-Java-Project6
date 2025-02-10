package com.paymybuddy.mvp.errors;

import static com.diffplug.selfie.Selfie.expectSelfie;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;

import org.junit.jupiter.api.Test;

class EErrorEmailTest {
    private final User userJohnDoe = HelperTest.johnDoe();

    @Test
    void Invalid() {
        final var actual =
                new EErrorEmail().new Invalid(userJohnDoe.getEmail().toString()).toString();

        expectSelfie(actual).toBe("""
Email error:
'john.doe@test.com' is not a valid email.""");
    }

    @Test
    void Null() {
        final var actual = new EErrorEmail().new Null().toString();

        expectSelfie(actual).toBe("""
Email error:
Email value is null.""");
    }
}
