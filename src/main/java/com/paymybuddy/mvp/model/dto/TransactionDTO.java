package com.paymybuddy.mvp.model.dto;

import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Money;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class TransactionDTO {
    @NonNull private Email maybeReceiver;
    @NonNull private Money change;
    private String description;
}
