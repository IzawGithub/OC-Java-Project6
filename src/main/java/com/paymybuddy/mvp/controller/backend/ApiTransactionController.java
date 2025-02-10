package com.paymybuddy.mvp.controller.backend;

import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.service.TransactionService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(
        value = "/api/transaction",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("isAuthenticated()")
public class ApiTransactionController {
    @PutMapping("/fromAppToBank")
    public @NonNull ResponseEntity<BankTransaction> tryFromAppToBank(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestBody @NonNull final Money change) {
        final var user = helperController.authToUser(userAuth);

        return transactionService
                .tryFromAppToBank(user, change)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .expect();
    }

    @PutMapping("/fromBankToApp")
    public @NonNull ResponseEntity<BankTransaction> fromBankToApp(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestBody @NonNull final Money change) {
        final var user = helperController.authToUser(userAuth);
        final var bankTransaction = transactionService.fromBankToApp(user, change);

        return new ResponseEntity<>(bankTransaction, HttpStatus.OK);
    }

    @PutMapping("/fromUserToUser")
    public @NonNull ResponseEntity<Transaction> tryfromAuthenticatedToUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestBody @NonNull final TransactionDTO transactionDto) {
        final var sender = helperController.authToUser(userAuth);

        return transactionService
                .tryFromUserToUser(sender, transactionDto)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .expect();
    }

    // -- Beans --

    @NonNull private final ApiAuthController helperController;

    @NonNull private final TransactionService transactionService;
}
