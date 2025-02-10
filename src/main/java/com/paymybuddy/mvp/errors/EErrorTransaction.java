package com.paymybuddy.mvp.errors;

import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.service.Localisation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

public sealed class EErrorTransaction extends EErrorPayMyBuddy {
    @EqualsAndHashCode(callSuper = true)
    @Value
    public class BalanceTooLow extends EErrorTransaction {
        @NonNull final User user;

        @NonNull final Money change;

        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "EErrorTransaction-bank-balance-too-low",
                            Map.ofEntries(
                                    Map.entry("amount", change),
                                    Map.entry("user", user.getEmail()),
                                    Map.entry("balance", user.getBalance()),
                                    Map.entry(
                                            "difference",
                                            user.getBalance()
                                                    .getUint()
                                                    .subtract(change.getUint())
                                                    .toString())));
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public class EqualId extends EErrorTransaction {
        @NonNull final User user;

        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "EErrorTransaction-equal-id",
                            Map.ofEntries(Map.entry("id", user.getId())));
        }
    }
}
