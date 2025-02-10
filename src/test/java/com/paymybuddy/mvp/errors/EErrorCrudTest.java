package com.paymybuddy.mvp.errors;

import static com.diffplug.selfie.Selfie.expectSelfie;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;

import org.junit.jupiter.api.Test;

class EErrorCrudTest {
    private final User userJohnDoe = HelperTest.johnDoe();

    @Test
    void AlreadyExist() {
        final var actual = new EErrorCrud()
                .new Exist(userJohnDoe.getId(), new EErrorCrud.EExist.Exist())
                .toString();

        expectSelfie(actual)
                .toBe(
                        """
CRUD error:
User '01234567-abcd-1abc-abcd-0123456789ab' already exist.""");
    }

    @Test
    void DoesNotExist() {
        final var actual = new EErrorCrud()
                .new Exist(userJohnDoe.getId(), new EErrorCrud.EExist.DoesNotExist())
                .toString();

        expectSelfie(actual)
                .toBe(
                        """
CRUD error:
User '01234567-abcd-1abc-abcd-0123456789ab' does not exist.""");
    }

    @Test
    void EqualsValue() {
        final var actual = new EErrorCrud().new EqualsValues(userJohnDoe.getEmail()).toString();

        expectSelfie(actual)
                .toBe(
                        """
CRUD error:
Operation cannot be done on the same user 'john.doe@test.com'.""");
    }
}
