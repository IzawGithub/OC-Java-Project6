package com.paymybuddy.mvp.controller.backend;

import com.paymybuddy.mvp.errors.EErrorAuth;
import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.service.UserService;

import lombok.AllArgsConstructor;

import net.xyzsd.dichotomy.Result;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(
        value = "/api/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("isAuthenticated()")
public class ApiAuthController {
    /**
     * Spring should basically assure us that we're always logged in. It would be better if there
     * was a way to get a User directly instead of a UserDetails.
     *
     * @param userAuth
     * @return
     */
    @GetMapping
    public @NonNull ResponseEntity<User> getAuthToUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        return new ResponseEntity<>(authToUser(userAuth), HttpStatus.OK);
    }

    public User authToUser(@NonNull final UserDetails userAuth) {
        return Email.builder()
                .email(userAuth.getUsername())
                .tryBuild()
                .mapErr(EErrorPayMyBuddy.class::cast)
                .flatMap(userService::tryReadUser)
                .flatMap(user -> {
                    // The password in userAuth might or might not be hashed and there's no
                    // real way to know beforehand
                    // For example, if the User is set manually and bypassing the
                    // UserDetailsService, it's not hashed
                    // IE: during tests, but maybe it's usersided
                    final var matchPasswordNotHashed =
                            user.getPassword().matches(userAuth.getPassword());
                    final var matchPasswordHashed =
                            user.getPassword().toString().equals(userAuth.getPassword());
                    // There's no XNOR operator :(
                    if (!(matchPasswordHashed ^ matchPasswordNotHashed)) {
                        return Result.ofErr(new EErrorAuth().new InvalidUsernameOrPassword());
                    }
                    return Result.ofOK(user);
                })
                .expect();
    }

    // -- Beans --

    @NonNull private UserService userService;
}
