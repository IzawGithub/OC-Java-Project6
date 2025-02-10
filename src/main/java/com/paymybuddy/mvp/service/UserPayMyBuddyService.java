package com.paymybuddy.mvp.service;

import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Secret;
import com.paymybuddy.mvp.repository.UserRepository;

import lombok.Getter;

import net.xyzsd.dichotomy.Result.Err;
import net.xyzsd.dichotomy.Result.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class UserPayMyBuddyService {
    @Getter private final User user;

    @Autowired
    public UserPayMyBuddyService(@NonNull final UserRepository userRepository) {
        final var email = Email.builder().email("user@paymybuddy.com").tryBuild().expect();
        final var resultUser = userRepository.tryFindByEmail(email);
        switch (resultUser) {
            case OK(User userPayMyBuddy) -> user = userPayMyBuddy;
            case Err(EErrorCrud err) -> {
                user = User.builder()
                        .email(
                                Email.builder()
                                        .email("user@paymybuddy.com")
                                        .tryBuild()
                                        .expect())
                        .username("PayMyBuddy")
                        .password(new Secret("PasswordPayMyBuddy"))
                        .role("ADMIN")
                        .build();

                userRepository.save(user);
            }
        }
    }
}
