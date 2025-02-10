package com.paymybuddy.mvp.model.internal;

import com.paymybuddy.mvp.errors.EErrorEmail;

import jakarta.persistence.Embeddable;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import net.xyzsd.dichotomy.Conversion;
import net.xyzsd.dichotomy.Result;
import net.xyzsd.dichotomy.trying.Try;

import java.util.regex.Pattern;

@NoArgsConstructor(force = true)
@Builder
@Embeddable
@Value
public class Email {
    @NonNull private String email;

    @Override
    public String toString() {
        return email;
    }

    public Email(String maybeEmail) throws EErrorEmail {
        this.email = Email.builder().email(maybeEmail).build().getEmail();
    }

    /**
     * This is the dumbest thing I have ever written, but unfortunately necessary.
     *
     * <p>Jackson deserialization doesn't work with @JsonUnwrapped, which would mean that we need an
     * API like this:
     *
     * <p>```json { "email": { "email": "UserEmail" } } ```
     *
     * <p>Obviously this is bad, so because we can't unwrap, we need to abuse constructor
     * overloading to set the email from the builder.
     *
     * @param email
     * @param privateValue
     */
    private Email(String email, boolean privateValue) {
        this.email = email;
    }

    public static class EmailBuilder {
        private static final Pattern REGEX_EMAIL = Pattern.compile(
                "^[a-zA-Z\\d.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?(?:\\.[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?)*$");

        public @NonNull Result<Email, EErrorEmail> tryBuild() {
            return Conversion.toResult(Try.wrap(this::build)).mapErr(EErrorEmail.class::cast);
        }

        public @NonNull Email build() throws EErrorEmail {
            return Result.ofNullable(this.email)
                    .mapErr(error -> (EErrorEmail) new EErrorEmail().new Null())
                    .flatMap(stringEmail -> {
                        if (!REGEX_EMAIL.matcher(this.email).matches()) {
                            return Result.ofErr(new EErrorEmail().new Invalid(email));
                        }
                        return Result.ofOK(new Email(email, true));
                    })
                    .getOrThrow(error -> error);
        }
    }
}
