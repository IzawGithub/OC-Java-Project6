package com.paymybuddy.mvp.controller.backend;

import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(
        value = "/api/user",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("isAuthenticated()")
public class ApiUserController {
    @PostMapping
    @PreAuthorize("isAnonymous()")
    public @NonNull ResponseEntity<User> tryCreateUser(
            @RequestBody @NonNull final UserDTO userDto) {
        return userService
                .tryCreateUser(userDto.toUser())
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .expect();
    }

    @PostMapping("/buddy")
    public @NonNull ResponseEntity<User> tryCreateBuddy(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestBody @NonNull final Email maybeBuddy) {
        final var user = helperController.authToUser(userAuth);

        return userService
                .tryCreateBuddy(user, maybeBuddy)
                .map(lambdaUser -> new ResponseEntity<>(lambdaUser, HttpStatus.OK))
                .expect();
    }

    @GetMapping
    public @NonNull ResponseEntity<User> readUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    public @NonNull ResponseEntity<User> tryUpdateUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth,
            @RequestBody @NonNull final UserUpdateDTO userUpdateDto) {
        final var userStart = helperController.authToUser(userAuth);

        return userService
                .tryUpdateUser(userStart, userUpdateDto)
                .map(userUpdated -> new ResponseEntity<>(userUpdated, HttpStatus.OK))
                .expect();
    }

    @DeleteMapping
    public @NonNull ResponseEntity<User> tryDeleteUser(
            @AuthenticationPrincipal @NonNull final UserDetails userAuth) {
        final var user = helperController.authToUser(userAuth);

        return userService
                .tryDeleteUser(user)
                .map(lambdaUser -> new ResponseEntity<>(lambdaUser, HttpStatus.OK))
                .expect();
    }

    // -- Beans --

    @NonNull private ApiAuthController helperController;

    @NonNull private UserService userService;
}
