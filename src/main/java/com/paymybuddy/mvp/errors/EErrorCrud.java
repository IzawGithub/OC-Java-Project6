package com.paymybuddy.mvp.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.paymybuddy.mvp.service.Localisation;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

public sealed class EErrorCrud extends EErrorPayMyBuddy {
    public sealed interface EExist {
        record Exist() implements EExist {
            @Override
            public Boolean asBool() {
                return true;
            }
        }

        record DoesNotExist() implements EExist {
            @Override
            public Boolean asBool() {
                return false;
            }
        }

        Boolean asBool();
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public class Exist extends EErrorCrud {
        @JsonUnwrapped
        @JsonProperty("id")
        @NonNull final Object id;

        @NonNull final EExist exist;

        @Override
        public String toString() {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "EErrorCrud-exist",
                            Map.ofEntries(
                                    Map.entry("id", id.toString()),
                                    Map.entry("exist", exist.asBool().toString())));
        }
    }
}
