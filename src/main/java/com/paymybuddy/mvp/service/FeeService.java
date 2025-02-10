package com.paymybuddy.mvp.service;

import com.paymybuddy.mvp.model.internal.Money;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Data
@Service
public class FeeService {
    @NonNull private Money feePercentage;

    @Autowired
    public FeeService() {
        this(Money.builder().uint(BigDecimal.valueOf(0.005)).tryBuild().expect());
    }

    public FeeService(@Value("${paymybuddy.bank.fee:0.005}") @NonNull final Money feePercentage) {
        this.feePercentage = feePercentage;
    }

    public @NonNull Money calculateFee(@NonNull final Money change) {
        return change.multiply(feePercentage);
    }

    public @NonNull Money calculateChangeMinusFee(@NonNull final Money change) {
        final var fee = calculateFee(change);
        // Because the minimum transaction amount is '0', there's no way to get a negative fee, so
        // this is always safe
        return change.substract(fee).expect();
    }
}
