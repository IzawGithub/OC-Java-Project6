package com.paymybuddy.mvp.model.internal;

import com.paymybuddy.mvp.model.User;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class UserTransaction {
    @NonNull private final User sender;

    @NonNull private final User receiver;

    @NonNull private final Money change;

    private final String description;
}
