package com.paymybuddy.mvp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@ActiveProfiles("test")
@DataJpaTest
class UserServiceTest {
    private final User userJohnDoe = HelperTest.johnDoe();
    private final UserDTO userJohnDoeDTO = HelperTest.johnDoeDTO();
    private final User userJaneDoe = HelperTest.janeDoe();
    private final UserDTO userJaneDoeDto = HelperTest.janeDoeDTO();

    // -- createUser --

    @Test
    void createUserErrorAlreadyExist() {
        final var expected =
                new EErrorCrud().new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.Exist());
        userRepository.save(userJohnDoe);

        final var actual = userService.tryCreateUser(userJohnDoe).err().get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isPresent());
    }

    @Test
    void createUserSuccess() {
        final var expected = userJohnDoe;
        final var actual = userService.tryCreateUser(userJohnDoe).expect();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isPresent());
    }

    // -- createBuddy --

    @Test
    void createBuddyDoesNotExist() {
        final var expected = new EErrorCrud()
        .new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist());
        final var actual = userService
                .tryCreateBuddy(userJohnDoe, userJohnDoe.getEmail())
                .err()
                .get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    @Test
    void createBuddyAlreadyExist() {
        userJohnDoe.getBuddies().add(userJaneDoe);

        final var expected =
                new EErrorCrud().new Exist(userJaneDoe.getEmail(), new EErrorCrud.EExist.Exist());
        final var actual =
                userService.tryCreateBuddy(userJohnDoe, userJaneDoe).err().get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    @Test
    void createBuddyEqualsValues() {
        final var expected = new EErrorCrud().new EqualsValues(userJohnDoe.getEmail());
        final var actual =
                userService.tryCreateBuddy(userJohnDoe, userJohnDoe).err().get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    @Test
    void createBuddySuccess() {
        userService.tryCreateBuddy(userJohnDoe, userJaneDoe).expect();

        final var actualJohnDoe =
                userRepository.findByEmail(userJohnDoe.getEmail()).get();
        final var actualJaneDoe =
                userRepository.findByEmail(userJaneDoe.getEmail()).get();

        assertEquals(userJohnDoe, actualJohnDoe);
        assertEquals(userJaneDoe, actualJaneDoe);
        assertTrue(userJohnDoe.getBuddies().contains(userJaneDoe));
    }

    @Test
    void createBuddySuccessWithEmail() {
        userRepository.save(userJaneDoe);
        userService.tryCreateBuddy(userJohnDoe, userJaneDoe.getEmail()).expect();

        final var actualJohnDoe =
                userRepository.findByEmail(userJohnDoe.getEmail()).get();
        final var actualJaneDoe =
                userRepository.findByEmail(userJaneDoe.getEmail()).get();

        assertEquals(userJohnDoe, actualJohnDoe);
        assertEquals(userJaneDoe, actualJaneDoe);
        assertTrue(userJohnDoe.getBuddies().contains(userJaneDoe));
    }

    // -- updateUser --

    @Test
    void updateUserDoesNotExist() {
        final var expected = new EErrorCrud()
        .new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist());
        final var actual = userService
                .tryUpdateUser(userJohnDoe, userJohnDoeDTO.toUserUpdateDto())
                .err()
                .get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    @Test
    void updateUserAlreadyExist() {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);

        final var expected =
                new EErrorCrud().new Exist(userJaneDoe.getEmail(), new EErrorCrud.EExist.Exist());
        final var actual = userService
                .tryUpdateUser(userJohnDoe, userJaneDoeDto.toUserUpdateDto())
                .err()
                .get();

        assertEquals(expected, actual);
        assertEquals(
                userJohnDoe,
                userRepository.tryFindByEmail(userJohnDoe.getEmail()).expect());
    }

    @Test
    void updateUserSuccessNoFields() {
        final var expected = userJohnDoe;

        userRepository.save(expected);
        userService.tryUpdateUser(expected, UserUpdateDTO.builder().build()).expect();

        final var actual = userRepository.findByEmail(expected.getEmail()).get();
        assertEquals(expected, actual);
        assertEquals(
                expected, userRepository.tryFindByEmail(expected.getEmail()).expect());
    }

    @Test
    void updateUserSuccessAllFields() {
        final var expectedStart = userJohnDoe;
        final var expected = userJaneDoe;
        expected.setId(expectedStart.getId());
        expected.setBalance(expectedStart.getBalance());

        userRepository.save(expectedStart);
        userService
                .tryUpdateUser(expectedStart, userJaneDoeDto.toUserUpdateDto())
                .expect();

        final var actual = userRepository.findByEmail(expected.getEmail()).get();
        assertEquals(expected, actual);
    }

    // -- deleteUser --

    @Test
    void deleteUserDoesNotExist() {
        final var expected = new EErrorCrud()
        .new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist());
        final var actual = userService.tryDeleteUser(userJohnDoe).err().get();

        assertEquals(expected, actual);
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    @Test
    void deleteUserSuccess() {
        final var toDelete = userJohnDoe;
        userRepository.save(toDelete);
        userService.tryDeleteUser(userJohnDoe);

        assertTrue(userRepository.findByEmail(toDelete.getEmail()).isEmpty());
    }

    // -- Beans --

    @NonNull private final UserRepository userRepository;

    @NonNull private final UserService userService;

    @Autowired
    UserServiceTest(
            @NonNull final PlatformTransactionManager transactionManager,
            @NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService =
                new UserService(new TransactionTemplate(transactionManager), userRepository);
    }
}
