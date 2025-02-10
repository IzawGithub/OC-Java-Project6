package com.paymybuddy.mvp.errors;

import com.paymybuddy.mvp.service.Localisation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

public sealed class EErrorMoney extends EErrorPayMyBuddy {
    @EqualsAndHashCode(callSuper = true)
    @Value
    public class NoNegative extends EErrorMoney {
        @NonNull final BigDecimal value;

        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format("EErrorMoney-no-negative", Map.of("value", value));
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public class Null extends EErrorMoney {
        @Override
        public String toString() {
            return Localisation.getInstance().getBundle().format("EErrorMoney-null");
        }
    }
}
