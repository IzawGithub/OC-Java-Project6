package com.paymybuddy.mvp.model.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embeddable;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * Represent a secret that should never be leaked.
 *
 * <p>This is a {@link <a href=
 * "https://doc.rust-lang.org/rust-by-example/generics/new_types.html">newtype</a>} around a
 * {@link String}.
 *
 * <p>This type make sure that the value given is always hashed with Argon2, this way, the password
 * can never be leaked, even by accident.
 */
@NoArgsConstructor(force = true)
@Embeddable
@Value
public class Secret {
    @JsonProperty("password")
    @NonNull private String secret;

    @Override
    public String toString() {
        return secret;
    }

    public Secret(final String rawSecret) {
        this.secret = PASSWORD_ENCODER.encode(rawSecret);
    }

    public boolean matches(final String rawSecret) {
        return PASSWORD_ENCODER.matches(rawSecret, secret);
    }

    // -- Interfaces --

    // Default defined by OWASP
    public static final Argon2PasswordEncoder PASSWORD_ENCODER =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
}
