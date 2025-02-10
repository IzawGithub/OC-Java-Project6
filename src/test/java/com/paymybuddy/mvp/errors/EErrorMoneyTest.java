package com.paymybuddy.mvp.errors;

import static com.diffplug.selfie.Selfie.expectSelfie;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;

import org.junit.jupiter.api.Test;

class EErrorMoneyTest {
    private static final User USER_JOHN_DOE = HelperTest.johnDoe();

    @Test
    void NoNegative() {
        final var actual =
                new EErrorMoney().new NoNegative(USER_JOHN_DOE.getBalance().getUint()).toString();

        expectSelfie(actual)
                .toBe("""
Money error:
Cannot represent a negative value as money.
\tValue: '0'""");
    }

    @Test
    void Null() {
        final var actual = new EErrorMoney().new Null().toString();

        expectSelfie(actual).toBe("""
Money error:
Money value is null.""");
    }
}
