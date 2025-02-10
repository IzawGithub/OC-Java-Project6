package com.paymybuddy.mvp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.internal.Money;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

class FeeServiceTest {
    private final Money money = HelperTest.money();
    private final Money percentage =
            Money.builder().uint(BigDecimal.valueOf(0.005)).tryBuild().expect();

    @Test
    void calculateFee() {
        final var expected =
                Money.builder().uint(BigDecimal.valueOf(0.06685)).tryBuild().expect();
        final var actual = feeService.calculateFee(money);

        assertEquals(expected, actual);
    }

    @Test
    void calculateChangeMinusFee() {
        final var expected =
                Money.builder().uint(BigDecimal.valueOf(13.30315)).tryBuild().expect();
        final var actual = feeService.calculateChangeMinusFee(money);

        assertEquals(expected, actual);
    }

    @Test
    void defaultPercentage() {
        final var expected = percentage;
        final var actual = new FeeService().getFeePercentage();

        assertEquals(expected, actual);
    }

    // -- Beans --

    @NonNull private final FeeService feeService;

    @Autowired
    FeeServiceTest() {
        this.feeService = new FeeService(percentage);
    }
}
