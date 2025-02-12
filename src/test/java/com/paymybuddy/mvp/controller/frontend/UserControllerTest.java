package com.paymybuddy.mvp.controller.frontend;

import static com.diffplug.selfie.Selfie.expectSelfie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.BankTransaction.ESens;
import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.dto.TransactionDTO.TransactionDTOBuilder;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.repository.BankTransactionRepository;
import com.paymybuddy.mvp.repository.TransactionRepository;
import com.paymybuddy.mvp.repository.UserRepository;
import com.paymybuddy.mvp.service.FeeService;

import jakarta.transaction.Transactional;

import lombok.experimental.ExtensionMethod;

import net.xyzsd.dichotomy.Maybe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@ExtensionMethod({HelperTest.class})
@SpringBootTest
@Transactional
@WithMockUser(username = "john.doe@test.com")
class UserControllerTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private UserUpdateDTO userJohnDoeUpdateDto = HelperTest.johnDoeDTO().toUserUpdateDto();
    private User userJaneDoe = HelperTest.janeDoe();
    private UserUpdateDTO userJaneDoeUpdateDto = HelperTest.janeDoeDTO().toUserUpdateDto();
    private Money money = HelperTest.money();
    private TransactionDTOBuilder transactionDtoBuilder =
            TransactionDTO.builder().change(money).description("Description");

    private final MockHttpServletRequestBuilder getUser = get("/user");
    private final MockHttpServletRequestBuilder postTransaction = post("/user/transaction")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());
    private final MockHttpServletRequestBuilder getBank = get("/user/bank");
    private final MockHttpServletRequestBuilder postFromAppToBank = post("/user/bank/fromAppToBank")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());
    private final MockHttpServletRequestBuilder postFromBankToApp = post("/user/bank/fromBankToApp")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());
    private final MockHttpServletRequestBuilder getProfil = get("/user/profil");
    private final MockHttpServletRequestBuilder getProfilForm = get("/user/profil/update");
    private final MockHttpServletRequestBuilder putProfilUpdate = put("/user/profil/update")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());
    private final MockHttpServletRequestBuilder deleteProfilUpdate = delete("/user/profil/update")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());
    private final MockHttpServletRequestBuilder getBuddy = get("/user/buddy");
    private final MockHttpServletRequestBuilder postBuddy = post("/user/buddy")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .with(csrf());

    @Test
    @WithAnonymousUser
    void userNotAuthenticated() throws Exception {
        mockMvc.perform(getUser).andExpectRedirectAuth();
    }

    @Test
    void user() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);

        final var uuid = UUID.fromString("01234567-abcd-1abc-abcd-0123456789ab");
        final var epochDate = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        bankTransactionRepository.save(BankTransaction.builder()
                .id(uuid)
                .amount(money)
                .sens(ESens.FROM_APP_TO_BANK)
                .user(userJohnDoe)
                .date(epochDate)
                .build());
        bankTransactionRepository.save(BankTransaction.builder()
                .id(uuid)
                .amount(money)
                .sens(ESens.FROM_BANK_TO_APP)
                .user(userJohnDoe)
                .date(epochDate)
                .build());
        transactionRepository.save(Transaction.builder()
                .id(uuid)
                .amount(money)
                .description("description")
                .sender(userJohnDoe)
                .receiver(userJaneDoe)
                .date(epochDate)
                .build());

        final var actualUnsanitized =
                mockMvc.perform(getUser).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualUnsanitized);

        expectSelfie(actual).toMatchDisk();
    }

    MockHttpServletRequestBuilder postTransactionParam(
            @NonNull final TransactionDTO transactionDto) {
        return postTransaction
                .param("email", transactionDto.getMaybeReceiver().toString())
                .param("change", transactionDto.getChange().toString())
                .param("description", transactionDto.getDescription());
    }

    @Test
    @WithAnonymousUser
    void transactionNotAuthenticated() throws Exception {
        userJohnDoe.setBalance(money);
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);
        final var transactionDto =
                transactionDtoBuilder.maybeReceiver(userJaneDoe.getEmail()).build();
        mockMvc.perform(postTransactionParam(transactionDto)).andExpectRedirectAuth();
    }

    @Test
    void transactionBalanceTooLow() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);
        final var expected = Money.builder().uint(BigDecimal.ZERO).tryBuild().expect();

        final var transactionDto =
                transactionDtoBuilder.maybeReceiver(userJaneDoe.getEmail()).build();
        mockMvc.perform(postTransactionParam(transactionDto)).andExpectRedirect("/user");

        final var actual = userJaneDoe.getBalance();
        assertEquals(expected, actual);
    }

    @Test
    void transactionSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);

        final var expectedBalanceJohnDoe =
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect();
        final var expectedBalanceJaneDoe = feeService.calculateChangeMinusFee(money);
        userJohnDoe.setBalance(money);

        final var transactionDto =
                transactionDtoBuilder.maybeReceiver(userJaneDoe.getEmail()).build();
        mockMvc.perform(postTransactionParam(transactionDto)).andExpectRedirect("/user");

        final var actualBalanceJohnDoe = userJohnDoe.getBalance();
        final var actualBalanceJaneDoe = userJaneDoe.getBalance();
        assertEquals(expectedBalanceJohnDoe, actualBalanceJohnDoe);
        assertEquals(expectedBalanceJaneDoe, actualBalanceJaneDoe);
    }

    @Test
    @WithAnonymousUser
    void bankNotAuthenticated() throws Exception {
        mockMvc.perform(getBank).andExpectRedirectAuth();
    }

    @Test
    void bank() throws Exception {
        userRepository.save(userJohnDoe);

        final var actualUnsanitized =
                mockMvc.perform(getBank).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualUnsanitized);

        expectSelfie(actual).toMatchDisk();
    }

    MockHttpServletRequestBuilder postBankParam(
            @NonNull final MockHttpServletRequestBuilder postBank, @NonNull final Money money) {
        return postBankParam(postBank, money.getUint());
    }

    MockHttpServletRequestBuilder postBankParam(
            @NonNull final MockHttpServletRequestBuilder postBank,
            @NonNull final BigDecimal money) {
        return postBank.param("change", money.toPlainString());
    }

    @Test
    @WithAnonymousUser
    void fromAppToBankNotAuthenticated() throws Exception {
        mockMvc.perform(postBankParam(postFromAppToBank, money)).andExpectRedirectAuth();
    }

    @Test
    void fromAppToBankIsNegative() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(money);
        mockMvc.perform(postBankParam(postFromAppToBank, BigDecimal.valueOf(-13.37)))
                .andExpectRedirect("/user/bank");
    }

    @Test
    void fromAppToBankSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var expectedBalance =
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect();
        userJohnDoe.setBalance(money);
        final var expectedBankTransaction = BankTransaction.builder()
                .amount(expectedBalance)
                .sens(ESens.FROM_APP_TO_BANK)
                .user(userJohnDoe)
                .build();

        mockMvc.perform(postBankParam(postFromAppToBank, money)).andExpectRedirect("/user");

        final var actualBalance = userJohnDoe.getBalance();
        final var actualBankTransaction =
                bankTransactionRepository.findAllByUser(userJohnDoe).getFirst();
        expectedBankTransaction.setId(actualBankTransaction.getId());
        expectedBankTransaction.setDate(actualBankTransaction.getDate());
        assertEquals(expectedBalance, actualBalance);
        assertEquals(expectedBankTransaction, actualBankTransaction);
    }

    @Test
    @WithAnonymousUser
    void fromBankToAppNotAuthenticated() throws Exception {
        mockMvc.perform(postBankParam(postFromBankToApp, money)).andExpectRedirectAuth();
    }

    @Test
    void fromBankToAppIsNegative() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        mockMvc.perform(postBankParam(postFromBankToApp, BigDecimal.valueOf(-13.37)))
                .andExpectRedirect("/user/bank");
    }

    @Test
    void fromBankToAppSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var expectedBalance = money;
        final var expectedBankTransaction = BankTransaction.builder()
                .amount(expectedBalance)
                .sens(ESens.FROM_APP_TO_BANK)
                .user(userJohnDoe)
                .build();

        mockMvc.perform(postBankParam(postFromBankToApp, expectedBalance))
                .andExpectRedirect("/user");

        final var actualBalance = userJohnDoe.getBalance();
        final var actualBankTransaction =
                bankTransactionRepository.findAllByUser(userJohnDoe).getFirst();
        expectedBankTransaction.setId(actualBankTransaction.getId());
        expectedBankTransaction.setDate(actualBankTransaction.getDate());
        assertEquals(expectedBalance, actualBalance);
        assertEquals(expectedBankTransaction, actualBankTransaction);
    }

    @Test
    @WithAnonymousUser
    void profilNotAuthenticated() throws Exception {
        mockMvc.perform(getProfil).andExpectRedirectAuth();
    }

    @Test
    void profil() throws Exception {
        userRepository.save(userJohnDoe);

        final var actualUnsanitized =
                mockMvc.perform(getProfil).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualUnsanitized);

        expectSelfie(actual).toMatchDisk();
    }

    @Test
    @WithAnonymousUser
    void profilFormNotAuthenticated() throws Exception {
        mockMvc.perform(getProfilForm).andExpectRedirectAuth();
    }

    @Test
    void profilForm() throws Exception {
        userRepository.save(userJohnDoe);

        final var actualUnsanitized =
                mockMvc.perform(getProfilForm).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualUnsanitized);

        expectSelfie(actual).toMatchDisk();
    }

    MockHttpServletRequestBuilder putProfilParam(@NonNull final UserUpdateDTO userUpdateDTO) {
        final var profilUpdate = putProfilUpdate;
        Maybe.ofNullable(userUpdateDTO.getUsername())
                .map(username -> profilUpdate.param("username", username));
        Maybe.ofNullable(userUpdateDTO.getEmail())
                .map(email -> profilUpdate.param("email", email.toString()));
        Maybe.ofNullable(userUpdateDTO.getPassword())
                .map(password -> profilUpdate.param("password", password.toString()));
        return profilUpdate;
    }

    @Test
    @WithAnonymousUser
    void profilUpdateNotAuthenticated() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        mockMvc.perform(putProfilParam(userJaneDoeUpdateDto)).andExpectRedirectAuth();
    }

    @Test
    void profilUpdateAlreadyExist() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);

        mockMvc.perform(putProfilParam(userJaneDoeUpdateDto))
                .andExpectRedirect("/user/profil/update");
    }

    @Test
    void profilUpdateNullSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var expected = userJohnDoe;

        mockMvc.perform(putProfilParam(UserUpdateDTO.builder().build()))
                .andExpectRedirect("/user/profil");

        final var actual = userRepository.findByEmail(userJohnDoe.getEmail()).get();
        assertEquals(expected, actual);
    }

    @Test
    void profilUpdateSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var expected = userJaneDoe;
        expected.setId(userJohnDoe.getId());

        mockMvc.perform(putProfilParam(userJaneDoeUpdateDto)).andExpectRedirect("/user/profil");

        final var actual =
                userRepository.findByEmail(userJaneDoeUpdateDto.getEmail()).get();
        assertEquals(expected, actual);
    }

    @Test
    @WithAnonymousUser
    void profilDeleteNotAuthenticated() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        mockMvc.perform(deleteProfilUpdate).andExpectRedirectAuth();
    }

    @Test
    void profilDelete() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var auth = mockMvc.perform(deleteProfilUpdate).expectAuth().expect();

        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
        assertNull(auth.getDetails());
    }

    @Test
    @WithAnonymousUser
    void buddyNotAuthenticated() throws Exception {
        mockMvc.perform(getBuddy).andExpectRedirectAuth();
    }

    @Test
    void buddy() throws Exception {
        userRepository.save(userJohnDoe);

        final var actualUnsanitized =
                mockMvc.perform(getBuddy).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualUnsanitized);

        expectSelfie(actual).toMatchDisk();
    }

    MockHttpServletRequestBuilder postBuddyParam(@NonNull final User user) {
        return postBuddyParam(user.getEmail());
    }

    MockHttpServletRequestBuilder postBuddyParam(@NonNull final Email emailUser) {
        return postBuddy.param("email", emailUser.getEmail());
    }

    @Test
    @WithAnonymousUser
    void postBuddyNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        mockMvc.perform(postBuddyParam(userJohnDoe)).andExpectRedirectAuth();
    }

    @Test
    void postBuddyAlreadyExist() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);

        mockMvc.perform(postBuddyParam(userJohnDoe)).andExpectRedirect("/user/buddy");

        assertFalse(userJohnDoe.getBuddies().contains(userJohnDoe));
    }

    @Test
    void postBuddySuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);

        mockMvc.perform(postBuddyParam(userJaneDoe)).andExpectRedirect("/user");

        assertTrue(userJohnDoe.getBuddies().contains(userJaneDoe));
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @NonNull private final FeeService feeService;

    @NonNull private final UserRepository userRepository;

    @NonNull private final BankTransactionRepository bankTransactionRepository;

    @NonNull private final TransactionRepository transactionRepository;

    @Autowired
    UserControllerTest(
            @NonNull final MockMvc mockMvc,
            @NonNull final UserRepository userRepository,
            @NonNull final BankTransactionRepository bankTransactionRepository,
            @NonNull final TransactionRepository transactionRepository) {
        this.mockMvc = mockMvc;
        this.feeService = new FeeService();
        this.userRepository = userRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.transactionRepository = transactionRepository;
    }
}
