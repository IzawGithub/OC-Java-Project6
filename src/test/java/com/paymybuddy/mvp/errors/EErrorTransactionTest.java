package com.paymybuddy.mvp.errors;

import static com.diffplug.selfie.Selfie.expectSelfie;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Money;

import org.junit.jupiter.api.Test;

class EErrorTransactionTest {
    private final User userJohnDoe = HelperTest.johnDoe();
    private final Money money = HelperTest.money();

    @Test
    void BalanceTooLow() {
        final var actual = new EErrorTransaction().new BalanceTooLow(userJohnDoe, money).toString();

        expectSelfie(actual)
                .toBe(
                        """
Transaction error:
Balance of user 'john.doe@test.com' is too low for the transaction.
\tCurrent balance: '0'
\tTransaction amount: '13.37'
\tDifference: '-13.37'""");
    }

    @Test
    void EqualId() {
        final var actual = new EErrorTransaction().new EqualId(userJohnDoe).toString();

        expectSelfie(actual)
                .toBe(
                        """
Transaction error:
Sender and receiver are the same user.
\tId: '01234567-abcd-1abc-abcd-0123456789ab'.""");
    }
}
