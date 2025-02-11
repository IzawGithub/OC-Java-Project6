package com.paymybuddy.mvp.controller.frontend;

import com.paymybuddy.mvp.controller.backend.ApiAuthController;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.service.TransactionService;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {
    @Getter
    @AllArgsConstructor
    public enum Navbar {
        BANK("/user/bank", RequestMethod.GET),
        BUDDIES("/user/buddy", RequestMethod.GET),
        PROFIL("/user/profil", RequestMethod.GET),
        DISCONNECT("/auth/log-out", RequestMethod.POST);

        @NonNull private final String path;

        @NonNull private final RequestMethod method;
    }

    @GetMapping
    public @NonNull ModelAndView home(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        final var transactions =
                transactionService.getTransactionRepository().findAllByUser(user).stream()
                        .map(transaction -> transaction.toUserShowTransactionDTO(user))
                        .toList();
        final var bankTransaction =
                transactionService.getBankTransactionRepository().findAllByUser(user).stream()
                        .map(BankTransaction::toUserShowTransactionDTO)
                        .toList();
        return new ModelAndView("/user/index.html")
                .addObject(user)
                .addObject("transactions", transactions)
                .addObject("bankTransactions", bankTransaction);
    }

    // -- Beans --

    @NonNull private ApiAuthController helperController;

    @NonNull private TransactionService transactionService;
}
