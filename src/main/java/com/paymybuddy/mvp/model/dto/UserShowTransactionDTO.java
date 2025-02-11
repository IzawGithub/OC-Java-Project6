package com.paymybuddy.mvp.model.dto;

import com.paymybuddy.mvp.model.User;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Value
public class UserShowTransactionDTO {
    private User user;
    private String description;

    @NonNull private BigDecimal amount;

    @NonNull private LocalDateTime date;
}
