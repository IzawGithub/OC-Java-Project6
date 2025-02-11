package com.paymybuddy.mvp.controller.frontend;

import com.paymybuddy.mvp.controller.backend.ApiAuthController;
import com.paymybuddy.mvp.errors.EErrorEmail;
import com.paymybuddy.mvp.errors.EErrorMoney;
import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.service.TransactionService;
import com.paymybuddy.mvp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.xyzsd.dichotomy.Result;
import net.xyzsd.dichotomy.Result.Err;
import net.xyzsd.dichotomy.Result.OK;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;

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

    @PostMapping("/transaction")
    public @NonNull ModelAndView fromUserToUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestParam(value = "email", required = false) final String maybeStringEmail,
            @RequestParam(value = "change", required = false) final BigDecimal maybeChange,
            @RequestParam(value = "description", required = false) final String maybeDescription,
            @NonNull final RedirectAttributes redirectAttributes) {
        final var user = helperController.authToUser(userAuth);

        final var resultEmail = Result.ofNullable(maybeStringEmail)
            .mapErr(err -> new EErrorEmail().new Null())
            .mapErr(EErrorPayMyBuddy.class::cast)
            .flatMap(stringEmail -> Email.builder()
                .email(stringEmail)
                .tryBuild()
                .mapErr(EErrorPayMyBuddy.class::cast)
        );
        final var resultChange = Result.ofNullable(maybeChange)
            .mapErr(err -> new EErrorMoney().new Null())
            .mapErr(EErrorPayMyBuddy.class::cast)
            .flatMap(change -> Money.builder()
                .uint(change)
                .tryBuild()
                .mapErr(EErrorPayMyBuddy.class::cast)
        );

        final var result = resultEmail
            .flatMap(email -> resultChange.map(change -> new SimpleEntry<>(email, change)))
            .map(tuple -> TransactionDTO.builder()
                .maybeReceiver(tuple.getKey())
                .change(tuple.getValue())
                .description(maybeDescription)
                .build()
            )
            .flatMap(transactionDTO -> transactionService.tryFromUserToUser(user, transactionDTO)
        );

        switch (result) {
            case OK(final var ok) -> {}
            case Err(final var err) -> redirectAttributes
                .addFlashAttribute("error", err);
        }

        return new ModelAndView("redirect:/user");
    }

    @GetMapping("/bank")
    public @NonNull ModelAndView bank(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        return new ModelAndView().addObject(user);
    }

    @PostMapping("/bank/fromAppToBank")
    public ModelAndView fromAppToBank(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestParam(value = "change", required = false) final BigDecimal maybeChange,
            @NonNull final RedirectAttributes redirectAttributes) {
        final var user = helperController.authToUser(userAuth);

        final var result = Result.ofNullable(maybeChange)
            .mapErr(err -> new EErrorMoney().new Null())
            .mapErr(EErrorPayMyBuddy.class::cast)
            .flatMap(change -> Money.builder()
            .uint(change)
            .tryBuild()
            .mapErr(EErrorPayMyBuddy.class::cast))
            .flatMap(money -> transactionService.tryFromAppToBank(user, money));
        return switch (result) {
            case OK(final var ok) -> new ModelAndView("redirect:/user");
            case Err(final var err) -> {
                redirectAttributes.addFlashAttribute("error", err);

                yield new ModelAndView("redirect:/user/bank");
            }
        };
    }

    @PostMapping("/bank/fromBankToApp")
    public ModelAndView fromBankToApp(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestParam(value = "change", required = false) final BigDecimal maybeChange,
            @NonNull final RedirectAttributes redirectAttributes) {
        final var user = helperController.authToUser(userAuth);

        final var result = Result.ofNullable(maybeChange)
            .mapErr(err -> new EErrorMoney().new Null())
            .mapErr(EErrorPayMyBuddy.class::cast)
            .flatMap(change -> Money.builder()
            .uint(change)
            .tryBuild()
            .mapErr(EErrorPayMyBuddy.class::cast))
            .map(money -> transactionService.fromBankToApp(user, money));
        return switch (result) {
            case OK(final var ok) -> new ModelAndView("redirect:/user");
            case Err(final var err) -> {
                redirectAttributes.addFlashAttribute("error", err);

                yield new ModelAndView("redirect:/user/bank");
            }
        };
    }

    @GetMapping("/profil")
    public @NonNull ModelAndView profil(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        return new ModelAndView().addObject(user);
    }

    @GetMapping("/profil/update")
    public @NonNull ModelAndView profilForm(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        return new ModelAndView().addObject(user);
    }

    @PutMapping("/profil/update")
    public @NonNull ModelAndView tryProfilUpdate(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @NonNull final UserUpdateDTO userUpdateDTO) {
        final var user = helperController.authToUser(userAuth);

        final var result = switch(maybeStringEmail) {
            case String stringEmail -> Email.builder()
                    .email(stringEmail)
                    .tryBuild()
                    .mapErr(EErrorPayMyBuddy.class::cast)
                    .map(email -> UserUpdateDTO.builder().username(username).email(email).password(password).build())
                    .flatMap(userUpdateDTO -> userService.tryUpdateUser(user, userUpdateDTO));

            case null -> {
                final var userUpdateDTO = UserUpdateDTO.builder().username(username).password(password).build();
                yield userService.tryUpdateUser(user, userUpdateDTO);
            }
        };
        return switch(result) {
            case OK(final var ok) -> new ModelAndView("redirect:/user/profil");
            case Err(final var err) -> {
                redirectAttributes.addFlashAttribute("error", err);

                yield new ModelAndView("redirect:/user/profil/update");
            }
        };
    }

    @DeleteMapping("/profil/update")
    public @NonNull ModelAndView tryProfilDelete(
            @NonNull final HttpServletRequest request,
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        userService.tryDeleteUser(user);

        // Logout programmatically, we can't redirect to /auth/log-out because it listen on POST
        final var session = request.getSession();
        session.invalidate();
        SecurityContextHolder.clearContext();
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/buddy")
    public @NonNull ModelAndView buddyForm() {
        return new ModelAndView();
    }

    @PostMapping("/buddy")
    public @NonNull ModelAndView tryBuddyAdd(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestParam(value = "email", required = false) final String maybeStringEmail,
            @NonNull final RedirectAttributes redirectAttributes) {
        final var user = helperController.authToUser(userAuth);

        final var result = Result.ofNullable(maybeStringEmail)
            .mapErr(err -> new EErrorEmail().new Null())
            .mapErr(EErrorPayMyBuddy.class::cast)
            .flatMap(stringEmail -> Email.builder()
                .email(stringEmail)
                .tryBuild()
                .mapErr(EErrorPayMyBuddy.class::cast)
            )
            .flatMap(maybeUser -> userService.tryCreateBuddy(user, maybeUser));
        return switch(result) {
            case OK(final var ok) -> new ModelAndView("redirect:/user");
            case Err(final var err) -> {
                redirectAttributes.addFlashAttribute("error", err);

                yield new ModelAndView("redirect:/user/buddy");
            }
        };
    }

    // -- Beans --

    @NonNull private UserService userService;

    @NonNull private ApiAuthController helperController;

    @NonNull private TransactionService transactionService;
}
