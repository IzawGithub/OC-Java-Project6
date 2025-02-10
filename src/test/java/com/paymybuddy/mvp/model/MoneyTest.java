package com.paymybuddy.mvp.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorMoney;
import com.paymybuddy.mvp.model.internal.Money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTest {
    private final BigDecimal invalidMoney = BigDecimal.valueOf(-13.37);
    private final BigDecimal validMoney = BigDecimal.valueOf(13.37);
    private final Money testMoney = HelperTest.money();

    @Test
    void errorNoNegative() {
        final var expected = new EErrorMoney().new NoNegative(invalidMoney);
        final var actual = Money.builder().uint(invalidMoney).tryBuild().err().get();

        assertEquals(expected, actual);
    }

    @Test
    void errorNull() {
        final var expected = new EErrorMoney().new Null();
        final var actual = Money.builder().tryBuild().err().get();

        assertEquals(expected, actual);
    }

    @Test
    void constructorError() {
        assertThrows(EErrorMoney.NoNegative.class, () -> new Money(invalidMoney));
    }

    @Test
    void emailSuccess() {
        final var expected = validMoney;
        final var actual = Money.builder().uint(validMoney).tryBuild().expect();

        assertEquals(expected, actual.getUint());
    }

    @Test
    void constructorSuccess() {
        assertDoesNotThrow(() -> new Money(validMoney));
    }

    @Test
    void constructorSuccessDouble() {
        assertDoesNotThrow(() -> new Money(13.37));
    }

    @Test
    void constructorSuccessString() {
        assertDoesNotThrow(() -> new Money("13.37"));
    }

    @Test
    void add() {
        final var expected = Money.builder()
                .uint(testMoney.getUint().add(testMoney.getUint()))
                .tryBuild()
                .expect();
        final var actual = testMoney.add(testMoney);
        assertEquals(expected, actual);
    }

    @Test
    void substractErrorNoNegative() {
        final var negative = Money.builder()
                .uint(BigDecimal.valueOf(Integer.MAX_VALUE))
                .tryBuild()
                .expect();

        final var expected = new EErrorMoney()
        .new NoNegative(testMoney.getUint().subtract(BigDecimal.valueOf(Integer.MAX_VALUE)));
        final var actual = testMoney.substract(negative).err().get();
        assertEquals(expected, actual);
    }

    @Test
    void substractSuccess() {
        final var expected =
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect();
        final var actual = testMoney.substract(testMoney).expect();
        assertEquals(expected, actual);
    }

    @Test
    void multiply() {
        final var expected = Money.builder()
                .uint(testMoney.getUint().multiply(testMoney.getUint()))
                .tryBuild()
                .expect();
        final var actual = testMoney.multiply(testMoney);
        assertEquals(expected, actual);
    }
}
