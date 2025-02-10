package com.paymybuddy.mvp.model;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paymybuddy.mvp.model.internal.Secret;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

class SecretTest {
    private static final Pattern REGEX_ARGON2 = Pattern.compile("\\$argon2.*");
    private final String unencryptedSecret = "Secret";

    @Test
    void secretIsEncoded() {
        final var secret = new Secret(unencryptedSecret);
        assertTrue(REGEX_ARGON2.matcher(secret.getSecret()).matches());
    }

    @Test
    void secretCanMatches() {
        final var secret = new Secret(unencryptedSecret);
        assertTrue(secret.matches(unencryptedSecret));
    }

    @Test
    void secretDoesntLeak() {
        final var secret = new Secret(unencryptedSecret);
        assertNotEquals(unencryptedSecret, secret.toString());
    }
}
