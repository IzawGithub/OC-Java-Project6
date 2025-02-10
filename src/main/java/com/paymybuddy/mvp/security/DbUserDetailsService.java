package com.paymybuddy.mvp.security;

import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

import net.xyzsd.dichotomy.Maybe;
import net.xyzsd.dichotomy.Result;

import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Data
@Service
public class DbUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(final String nullableStringEmail)
            throws UsernameNotFoundException {
        final var stringEmail = Maybe.ofNullable(nullableStringEmail)
                .getOrThrow(() -> new UsernameNotFoundException("Field is null"));
        return Email.builder()
                .email(stringEmail)
                .tryBuild()
                .mapErr(EErrorPayMyBuddy.class::cast)
                .flatMap(email -> Result.from(userRepository.findByEmail(email))
                        .mapErr(unnamned -> new EErrorCrud()
                        .new Exist(email.getEmail(), new EErrorCrud.EExist.DoesNotExist())))
                .map(user -> {
                    final var authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail().toString(), user.getPassword().toString(), authorities);
                })
                .getOrThrow(error -> new UsernameNotFoundException(error.toString()));
    }

    // -- Beans --

    @NonNull private UserRepository userRepository;
}
