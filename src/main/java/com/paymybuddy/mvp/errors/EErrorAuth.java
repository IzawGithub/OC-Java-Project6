package com.paymybuddy.mvp.errors;

import com.paymybuddy.mvp.service.Localisation;

import lombok.EqualsAndHashCode;
import lombok.Value;

public sealed class EErrorAuth extends EErrorPayMyBuddy {
    @EqualsAndHashCode(callSuper = true)
    @Value
    public class InvalidUsernameOrPassword extends EErrorAuth {
        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format("EErrorAuth-invalid-username-or-password");
        }
    }
}
