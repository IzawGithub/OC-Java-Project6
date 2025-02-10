package com.paymybuddy.mvp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.errors.EErrorTransaction;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.BankTransaction.ESens;
import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.dto.TransactionDTO.TransactionDTOBuilder;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.repository.BankTransactionRepository;
import com.paymybuddy.mvp.repository.TransactionRepository;
import com.paymybuddy.mvp.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@ActiveProfiles("test")
@DataJpaTest
class TransactionServiceTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private User userJaneDoe = HelperTest.janeDoe();
    private Money testMoney = HelperTest.money();

    private TransactionDTOBuilder transaction =
            TransactionDTO.builder().change(HelperTest.money()).description("Description");

    @Test
    void fromAppToBankNotEnoughCash() {
        final var expected = new EErrorTransaction().new BalanceTooLow(userJohnDoe, testMoney);
        final var actual = transactionService
                .tryFromAppToBank(userJohnDoe, testMoney)
                .err()
                .get();
        assertEquals(expected, actual);
    }

    @Test
    void fromAppToBankSuccess() {
        userJohnDoe.setBalance(testMoney);

        final var expected = BankTransaction.builder()
                .amount(testMoney)
                .sens(ESens.FROM_APP_TO_BANK)
                .user(userJohnDoe)
                .build();
        final var actual =
                transactionService.tryFromAppToBank(userJohnDoe, testMoney).expect();
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
    }

    @Test
    void fromBankToApp() {
        userJohnDoe.setBalance(testMoney);

        final var expected = BankTransaction.builder()
                .amount(testMoney)
                .sens(ESens.FROM_BANK_TO_APP)
                .user(userJohnDoe)
                .build();
        final var actual = transactionService.fromBankToApp(userJohnDoe, testMoney);
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
    }

    @Test
    void fromUserToUserDoesNotExist() {
        final var expected = new EErrorCrud()
        .new Exist(userJaneDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist());
        final var actual = transactionService
                .tryFromUserToUser(
                        userJohnDoe,
                        transaction.maybeReceiver(userJaneDoe.getEmail()).build())
                .err()
                .get();
        assertEquals(expected, actual);
    }

    @Test
    void fromUserToUserEqualId() {
        userJohnDoe = userRepository.save(userJohnDoe);

        final var expected = new EErrorTransaction().new EqualId(userJohnDoe);
        final var actual = transactionService
                .tryFromUserToUser(
                        userJohnDoe,
                        transaction.maybeReceiver(userJohnDoe.getEmail()).build())
                .err()
                .get();
        assertEquals(expected, actual);
    }

    @Test
    void fromUserToUserBalanceTooLow() {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);

        final var expected = new EErrorTransaction().new BalanceTooLow(userJohnDoe, testMoney);
        final var actual = transactionService
                .tryFromUserToUser(
                        userJohnDoe,
                        transaction.maybeReceiver(userJaneDoe.getEmail()).build())
                .err()
                .get();
        assertEquals(expected, actual);
    }

    @Test
    void fromUserToUserSuccess() {
        userJohnDoe.setBalance(testMoney);
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);

        final var expected = Transaction.builder()
                .amount(testMoney)
                .description(transaction.maybeReceiver(userJaneDoe.getEmail()).build().getDescription())
                .sender(userJohnDoe)
                .receiver(userJaneDoe)
                .build();
        final var expectedJohnDoe =
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect();
        final var expectedJaneDoe = feeService.calculateChangeMinusFee(testMoney);
        final var actual = transactionService
                .tryFromUserToUser(
                        userJohnDoe,
                        transaction.maybeReceiver(userJaneDoe.getEmail()).build())
                .expect();
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
        assertEquals(expectedJohnDoe, userJohnDoe.getBalance());
        assertEquals(expectedJaneDoe, userJaneDoe.getBalance());
    }

    // -- Beans --

    @NonNull private final UserRepository userRepository;

    @NonNull private final FeeService feeService;

    @NonNull private final TransactionService transactionService;

    @Autowired
    TransactionServiceTest(
            @NonNull final PlatformTransactionManager transactionManager,
            @NonNull final UserRepository userRepository,
            @NonNull final BankTransactionRepository bankTransactionRepository,
            @NonNull final TransactionRepository transactionRepository) {
        final var userPayMyBuddy = new UserPayMyBuddyService(userRepository);
        this.userRepository = userRepository;
        this.feeService = new FeeService();
        transactionService = new TransactionService(
                transactionManager,
                bankTransactionRepository,
                userRepository,
                transactionRepository,
                feeService,
                userPayMyBuddy);
    }
}
