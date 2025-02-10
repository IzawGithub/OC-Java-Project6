package com.paymybuddy.mvp;

import com.paymybuddy.mvp.model.internal.Money;

import java.math.BigDecimal;

public final class HelperTest {
    public static Money money() {
        return Money.builder().uint(new BigDecimal("13.37")).tryBuild().expect();
    }
}
