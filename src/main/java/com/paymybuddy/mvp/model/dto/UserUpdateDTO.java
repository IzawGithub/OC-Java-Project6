package com.paymybuddy.mvp.model.dto;

import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Secret;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserUpdateDTO {
    private String username;
    private Email email;
    private Secret password;
}
