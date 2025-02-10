package com.paymybuddy.mvp.repository;

import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.internal.Email;

import net.xyzsd.dichotomy.Conversion;
import net.xyzsd.dichotomy.Result;
import net.xyzsd.dichotomy.trying.Try;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(Email email);

    default Result<User, EErrorCrud> tryFindById(@NonNull final UUID id) {
        return Conversion.toResult(Try.wrap(() -> findById(id).get()))
                .mapErr(unnamned ->
                        new EErrorCrud().new Exist(id, new EErrorCrud.EExist.DoesNotExist()));
    }

    default Result<User, EErrorCrud> tryFindByEmail(@NonNull final Email email) {
        return Conversion.toResult(Try.wrap(() -> findByEmail(email).get()))
                .mapErr(unnamned ->
                        new EErrorCrud().new Exist(email, new EErrorCrud.EExist.DoesNotExist()));
    }
}
