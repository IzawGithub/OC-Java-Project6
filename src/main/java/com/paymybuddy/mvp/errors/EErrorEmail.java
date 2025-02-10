package com.paymybuddy.mvp.errors;

import com.paymybuddy.mvp.service.Localisation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

public sealed class EErrorEmail extends EErrorPayMyBuddy {
    @EqualsAndHashCode(callSuper = true)
    @Value
    public class Invalid extends EErrorEmail {
        @NonNull final String invalidEmail;

        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format("EErrorEmail-invalid", Map.ofEntries(Map.entry("email", invalidEmail)));
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public class Null extends EErrorEmail {
        @Override
        public String toString() {
            return Localisation.getInstance().getBundle().format("EErrorEmail-null");
        }
    }
}
