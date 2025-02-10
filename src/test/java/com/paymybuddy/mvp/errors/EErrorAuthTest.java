package com.paymybuddy.mvp.errors;

import static com.diffplug.selfie.Selfie.expectSelfie;

import org.junit.jupiter.api.Test;

class EErrorAuthTest {
    @Test
    void InvalidUsernameOrPassword() {
        final var actual = new EErrorAuth().new InvalidUsernameOrPassword().toString();

        expectSelfie(actual).toBe("""
Authentication error:
Invalid username and/or password.""");
    }
}
