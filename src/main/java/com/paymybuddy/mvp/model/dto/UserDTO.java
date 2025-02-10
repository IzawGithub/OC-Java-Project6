package com.paymybuddy.mvp.model.dto;

import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Secret;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class UserDTO {
    @NonNull private String username;

    @NonNull private Email email;

    @NonNull private String password;

    public User toUser() {
        return User.builder()
                .username(username)
                .email(email)
                .password(new Secret(password))
                .role("USER")
                .build();
    }

    /** {@link UserUpdateDTO} allows nulls. */
    public UserUpdateDTO toUserUpdateDto() {
        return UserUpdateDTO.builder()
                .username(username)
                .email(email)
                .password(new Secret(password))
                .build();
    }
}
