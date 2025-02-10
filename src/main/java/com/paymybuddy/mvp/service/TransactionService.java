package com.paymybuddy.mvp.service;

import com.paymybuddy.mvp.errors.*;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.BankTransaction.ESens;
import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.model.internal.UserTransaction;
import com.paymybuddy.mvp.repository.BankTransactionRepository;
import com.paymybuddy.mvp.repository.TransactionRepository;
import com.paymybuddy.mvp.repository.UserRepository;

import lombok.Getter;

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

import java.util.Map;

@Service
public class TransactionService {
    static class InnerLocalisation {
        private InnerLocalisation() {}

        static String bankSuccessInfo(@NonNull final BankTransaction bankTransaction) {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "TransactionService-bank-success-info",
                            Map.ofEntries(
                                    Map.entry("user", bankTransaction.getUser().getEmail())));
        }

        static String userSuccessInfo(@NonNull final Transaction transaction) {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "TransactionService-user-success-info",
                            Map.ofEntries(
                                    Map.entry(
                                            "id_sender", transaction.getSender().getEmail()),
                                    Map.entry("change", transaction.getAmount()),
                                    Map.entry(
                                            "id_receiver",
                                            transaction.getReceiver().getEmail()),
                                    Map.entry(
                                            "sender_balance_old",
                                            transaction
                                                    .getSender()
                                                    .getBalance()
                                                    .getUint()
                                                    .add(transaction.getAmount().getUint())),
                                    Map.entry(
                                            "sender_balance_new",
                                            transaction.getSender().getBalance()),
                                    Map.entry(
                                            "receiver_balance_old",
                                            transaction
                                                    .getReceiver()
                                                    .getBalance()
                                                    .getUint()
                                                    .subtract(transaction
                                                            .getAmount()
                                                            .getUint())),
                                    Map.entry(
                                            "receiver_balance_new",
                                            transaction.getReceiver().getBalance())));
        }

        static String moneyExchangeInfo(
                @NonNull final User user,
                @NonNull final Money change,
                @NonNull final EArithmeticOp transactionType) {
            return Localisation.getInstance()
                    .getBundle()
                    .format(
                            "TransactionService-money-exchange-info",
                            Map.ofEntries(
                                    Map.entry("id", user.getEmail()),
                                    Map.entry("change", change),
                                    Map.entry("transactionType", transactionType.toString())));
        }
    }

    sealed interface EArithmeticOp {
        ESens toESens();

        record Add() implements EArithmeticOp {
            @Override
            public String toString() {
                return "add";
            }

            @Override
            public ESens toESens() {
                return ESens.FROM_BANK_TO_APP;
            }
        }

        record Substract() implements EArithmeticOp {
            @Override
            public String toString() {
                return "substract";
            }

            @Override
            public ESens toESens() {
                return ESens.FROM_APP_TO_BANK;
            }
        }
    }

    /**
     * Try to remove money from PayMyBuddy and send it to the user bank.
     *
     * @param User User that will receive money to their bank account.
     * @param change Amount of money that will be received.
     * @return Ok(BankTransaction) if success, Err(EErrorTransaction) otherwise.
     */
    public @NonNull Result<BankTransaction, EErrorTransaction> tryFromAppToBank(
            @NonNull final User user, @NonNull final Money change) {
        return tryFromSomewhereToSomewhere(user, change, new EArithmeticOp.Substract());
    }

    /**
     * Add money to PayMyBuddy and from the user bank.
     *
     * @param user User that will receive money to their app account.
     * @param change Amount of money that will be received.
     * @return BankTransaction
     */
    public @NonNull BankTransaction fromBankToApp(
            @NonNull final User user, @NonNull final Money change) {
        // This can never fail, because the only failure point would be removing money.
        // Of course, we're only adding to the account, so it's always safe
        return tryFromSomewhereToSomewhere(user, change, new EArithmeticOp.Add())
                .expect();
    }

    /**
     * Handle logic for sending money from and to a bank.
     *
     * @param User User that will send/receive money
     * @param change Amount of money that will be exchanged.
     * @param transactionType Type of the transaction. Can be either: - EArithmeticOp.Add: Add the
     *     change amount to the user account - EArithmeticOp.Substract: Remove the change amount
     *     from the user account
     * @return
     */
    @SuppressWarnings("null")
    private @NonNull Result<BankTransaction, EErrorTransaction> tryFromSomewhereToSomewhere(
            @NonNull final User user,
            @NonNull final Money change,
            @NonNull final EArithmeticOp transactionType) {
        return transactionTemplate.<Result<BankTransaction, EErrorTransaction>>execute(
                new TransactionCallback<Result<BankTransaction, EErrorTransaction>>() {
                    @Override
                    @NonNull public Result<BankTransaction, EErrorTransaction> doInTransaction(
                            TransactionStatus status) {
                        return moneyExchange(user, change, transactionType)
                                .map(lambdaUser -> {
                                    final var bankTransaction = BankTransaction.builder()
                                            .amount(change)
                                            .user(lambdaUser)
                                            .sens(transactionType.toESens())
                                            .build();
                                    bankTransactionRepository.save(bankTransaction);

                                    return bankTransaction;
                                })
                                .match(bankTransaction -> logger.info(
                                        InnerLocalisation.bankSuccessInfo(bankTransaction)))
                                .matchErr(error -> {
                                    status.setRollbackOnly();
                                    logger.error(error.toString());
                                });
                    }
                });
    }

    /**
     * Try to send money between 2 user.
     *
     * @param sender User that will send the money.
     * @param maybeReceiver Potential user that will receive the money.
     * @param change Amount of money that will be exchanged.
     * @return Ok(Transaction) if succesfull, Err(EErrorPayMyBuddy) otherwise.
     * @throws EErrorMoney
     */
    @SuppressWarnings("null")
    public @NonNull Result<Transaction, EErrorPayMyBuddy> tryFromUserToUser(
            @NonNull final User sender, @NonNull final TransactionDTO transactionDTO) {
        return transactionTemplate.<Result<Transaction, EErrorPayMyBuddy>>execute(
                new TransactionCallback<Result<Transaction, EErrorPayMyBuddy>>() {
                    @Override
                    @NonNull public Result<Transaction, EErrorPayMyBuddy> doInTransaction(
                            TransactionStatus status) {
                        return userRepository
                                .tryFindByEmail(transactionDTO.getMaybeReceiver())
                                .map(receiver -> UserTransaction.builder()
                                        .sender(sender)
                                        .receiver(receiver)
                                        .change(transactionDTO.getChange())
                                        .description(transactionDTO.getDescription())
                                        .build())
                                .mapErr(EErrorPayMyBuddy.class::cast)
                                .flatMap(this::checkBothNotSameUser)
                                .flatMap(this::addFeeToPayMyBuddyAccount)
                                .flatMap(this::transferMoneyFromUserAccounts)
                                .matchErr(error -> {
                                    status.setRollbackOnly();
                                    logger.error(error.toString());
                                });
                    }

                    private @NonNull Result<UserTransaction, EErrorTransaction>
                            checkBothNotSameUser(
                                    @NonNull final UserTransaction userTransactionDto) {
                        if (userTransactionDto
                                .getSender()
                                .equals(userTransactionDto.getReceiver())) {
                            return Result.ofErr(new EErrorTransaction()
                            .new EqualId(userTransactionDto.getSender()));
                        }
                        return Result.ofOK(userTransactionDto);
                    }

                    private @NonNull Result<UserTransaction, EErrorTransaction>
                            addFeeToPayMyBuddyAccount(
                                    @NonNull final UserTransaction userTransactionDTO) {
                        final var fee = feeService.calculateFee(userTransactionDTO.getChange());

                        return moneyExchange(userPayMyBuddy.getUser(), fee, new EArithmeticOp.Add())
                                .map(user -> {
                                    final var transaction = Transaction.builder()
                                            .amount(fee)
                                            .description("PayMyBuddy fee")
                                            .sender(userTransactionDTO.getSender())
                                            .receiver(userPayMyBuddy.getUser())
                                            .build();
                                    transactionRepository.save(transaction);
                                    return userTransactionDTO;
                                });
                    }

                    private @NonNull Result<Transaction, EErrorTransaction>
                            transferMoneyFromUserAccounts(
                                    @NonNull final UserTransaction userTransactionDTO) {
                        final var changeMinusFee =
                                feeService.calculateChangeMinusFee(userTransactionDTO.getChange());

                        return moneyExchange(
                                        userTransactionDTO.getSender(),
                                        // Remove the full amount from the sender
                                        userTransactionDTO.getChange(),
                                        new EArithmeticOp.Substract())
                                .flatMap(user -> moneyExchange(
                                        userTransactionDTO.getReceiver(),
                                        changeMinusFee,
                                        new EArithmeticOp.Add()))
                                .map(user -> {
                                    final var transaction = Transaction.builder()
                                            .amount(changeMinusFee)
                                            .description(userTransactionDTO.getDescription())
                                            .sender(userTransactionDTO.getSender())
                                            .receiver(userTransactionDTO.getReceiver())
                                            .build();
                                    transactionRepository.save(transaction);

                                    return transaction;
                                })
                                .match(transaction -> logger.info(
                                        InnerLocalisation.userSuccessInfo(transaction)));
                    }
                });
    }

    /**
     * Change the balance of a user account using a transaction.
     *
     * @param user user that gets changed by the transaction.
     * @param change Amount of the transaction.
     * @param transactionType Type of the transaction. Can be either:
     *     <p>- EArithmeticOp.Add: Add the change amount to the user account
     *     <p>- EArithmeticOp.Substract: Remove the change amount from the user account
     * @return Ok(User) or Err(EErrorMoney).
     */
    private @NonNull Result<User, EErrorTransaction> moneyExchange(
            @NonNull final User user,
            @NonNull final Money change,
            @NonNull final EArithmeticOp transactionType) {
        // Store the result
        final var resultBalance =
                switch (transactionType) {
                    case EArithmeticOp.Add arithmeticAdd -> Result.ofOK(
                                    user.getBalance().add(change))
                            .mapErr(EErrorTransaction.class::cast);
                    case EArithmeticOp.Substract arithmeticSubstract -> user.getBalance()
                            .substract(change)
                            .mapErr(error -> (EErrorTransaction)
                                    new EErrorTransaction().new BalanceTooLow(user, change));
                };

        return resultBalance
                .map(endBalance -> {
                    user.setBalance(endBalance);
                    userRepository.save(user);

                    return user;
                })
                .match(resultUser -> logger.info(
                        InnerLocalisation.moneyExchangeInfo(resultUser, change, transactionType)));
    }

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // -- Beans --

    // We can't use @Transactional because of the `Result` monad
    // It only trigger if there's a runtime exception, and a Result.ofErr() isn't one!
    @NonNull private final TransactionTemplate transactionTemplate;

    @Getter
    @NonNull private final BankTransactionRepository bankTransactionRepository;

    @NonNull private final UserRepository userRepository;

    @Getter
    @NonNull private final TransactionRepository transactionRepository;

    @NonNull private final FeeService feeService;

    @NonNull private final UserPayMyBuddyService userPayMyBuddy;

    @Autowired
    public TransactionService(
            @NonNull final PlatformTransactionManager transactionTemplate,
            @NonNull final BankTransactionRepository bankTransactionRepository,
            @NonNull final UserRepository userRepository,
            @NonNull final TransactionRepository transactionRepository,
            @NonNull final FeeService feeService,
            @NonNull final UserPayMyBuddyService userPayMyBuddy) {
        this.transactionTemplate = new TransactionTemplate(transactionTemplate);
        this.bankTransactionRepository = bankTransactionRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.feeService = feeService;
        this.userPayMyBuddy = userPayMyBuddy;
    }
}
