package com.paymybuddy.mvp.service;

import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.repository.UserRepository;

import lombok.AllArgsConstructor;

import net.xyzsd.dichotomy.Maybe;
import net.xyzsd.dichotomy.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService {
    @SuppressWarnings("null")
    public @NonNull Result<User, EErrorCrud> tryCreateUser(@NonNull final User user) {
        return transactionTemplate.<Result<User, EErrorCrud>>execute(
                new TransactionCallback<Result<User, EErrorCrud>>() {
                    @Override
                    @NonNull public Result<User, EErrorCrud> doInTransaction(TransactionStatus status) {
                        return tryReadUser(user)
                                .swap()
                                .mapErr(error -> (EErrorCrud) new EErrorCrud()
                                .new Exist(user.getEmail(), new EErrorCrud.EExist.Exist()))
                                .map(unnamned -> userRepository.save(user))
                                .matchErr(error -> {
                                    status.setRollbackOnly();
                                    logger.error(error.toString());
                                });
                    }
                });
    }

    public @NonNull Result<User, EErrorCrud> tryCreateBuddy(
            @NonNull final User user, @NonNull final Email maybeBuddy) {
        return tryReadUser(maybeBuddy).flatMap(userBuddy -> tryCreateBuddy(user, userBuddy));
    }

    @SuppressWarnings("null")
    public @NonNull Result<User, EErrorCrud> tryCreateBuddy(
            @NonNull final User user, @NonNull final User buddy) {
        return transactionTemplate.<Result<User, EErrorCrud>>execute(
                new TransactionCallback<Result<User, EErrorCrud>>() {
                    @Override
                    @NonNull public Result<User, EErrorCrud> doInTransaction(TransactionStatus status) {
                        if (user.getEmail().equals(buddy.getEmail())) {
                            return Result.<User, EErrorCrud>ofErr(
                                            new EErrorCrud().new EqualsValues(user.getEmail()))
                                    .matchErr(error -> {
                                        status.setRollbackOnly();
                                        logger.error(error.toString());
                                    });
                        }
                        // Check that the user doesn't already know this buddy
                        if (user.getBuddies().contains(buddy)) {
                            return Result.<User, EErrorCrud>ofErr(new EErrorCrud()
                                    .new Exist(buddy.getEmail(), new EErrorCrud.EExist.Exist()))
                                    .matchErr(error -> {
                                        status.setRollbackOnly();
                                        logger.error(error.toString());
                                    });
                        }
                        user.getBuddies().add(buddy);
                        userRepository.save(buddy);
                        userRepository.save(user);
                        return Result.ofOK(user);
                    }
                });
    }

    public @NonNull Result<User, EErrorCrud> tryReadUser(@NonNull final User user) {
        return tryReadUser(user.getEmail());
    }

    public @NonNull Result<User, EErrorCrud> tryReadUser(@NonNull final Email email) {
        return userRepository.tryFindByEmail(email);
    }

    @SuppressWarnings("null")
    public @NonNull Result<User, EErrorCrud> tryUpdateUser(
            @NonNull final User userStart, @NonNull final UserUpdateDTO userUpdateDTO) {
        return transactionTemplate.<Result<User, EErrorCrud>>execute(
                new TransactionCallback<Result<User, EErrorCrud>>() {
                    @Override
                    @NonNull public Result<User, EErrorCrud> doInTransaction(TransactionStatus status) {
                        return tryReadUser(userStart.getEmail())
                                .mapErr(error -> new EErrorCrud()
                                .new Exist(
                                        userStart.getEmail(), new EErrorCrud.EExist.DoesNotExist()))
                                .mapErr(EErrorCrud.class::cast)
                                // Can't change email to an email that already exist
                                .flatMap(lambdaUser -> {
                                    if (Objects.nonNull(userUpdateDTO.getEmail())) {
                                        return tryReadUser(userUpdateDTO.getEmail())
                                                .swap()
                                                .map(error -> lambdaUser)
                                                .mapErr(userAlreadyExist -> new EErrorCrud()
                                                .new Exist(
                                                        userAlreadyExist.getEmail(),
                                                        new EErrorCrud.EExist.Exist()));
                                    }
                                    return Result.ofOK(lambdaUser);
                                })
                                .map(lambdaUser -> {
                                    Maybe.ofNullable(userUpdateDTO.getUsername())
                                            .match(lambdaUser::setUsername);
                                    Maybe.ofNullable(userUpdateDTO.getEmail())
                                            .match(lambdaUser::setEmail);
                                    Maybe.ofNullable(userUpdateDTO.getPassword())
                                            .match(lambdaUser::setPassword);

                                    userRepository.save(lambdaUser);
                                    return lambdaUser;
                                })
                                .matchErr(error -> {
                                    status.setRollbackOnly();
                                    logger.error(error.toString());
                                });
                    }
                });
    }

    @SuppressWarnings("null")
    public @NonNull Result<User, EErrorCrud> tryDeleteUser(@NonNull final User user) {
        return transactionTemplate.<Result<User, EErrorCrud>>execute(
                new TransactionCallback<Result<User, EErrorCrud>>() {
                    @Override
                    @NonNull public Result<User, EErrorCrud> doInTransaction(TransactionStatus status) {
                        return tryReadUser(user.getEmail())
                                .mapErr(error -> (EErrorCrud) new EErrorCrud()
                                .new Exist(user.getEmail(), new EErrorCrud.EExist.DoesNotExist()))
                                .map(lambdaUser -> {
                                    userRepository.delete(lambdaUser);
                                    return lambdaUser;
                                })
                                .matchErr(error -> {
                                    status.setRollbackOnly();
                                    logger.error(error.toString());
                                });
                    }
                });
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // -- Beans --

    @NonNull private final TransactionTemplate transactionTemplate;

    @NonNull private final UserRepository userRepository;

    @Autowired
    public UserService(
            @NonNull final PlatformTransactionManager transactionTemplate,
            @NonNull final UserRepository userRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.userRepository = userRepository;
    }
}
