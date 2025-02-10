package com.paymybuddy.mvp.controller.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.errors.EErrorTransaction;
import com.paymybuddy.mvp.model.BankTransaction;
import com.paymybuddy.mvp.model.BankTransaction.ESens;
import com.paymybuddy.mvp.model.Transaction;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.ResultDTO;
import com.paymybuddy.mvp.model.dto.TransactionDTO;
import com.paymybuddy.mvp.model.dto.TransactionDTO.TransactionDTOBuilder;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.repository.UserRepository;
import com.paymybuddy.mvp.service.FeeService;

import jakarta.transaction.Transactional;

import lombok.experimental.ExtensionMethod;

import net.xyzsd.dichotomy.Result;

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

@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@ExtensionMethod({HelperTest.class})
@SpringBootTest
@Transactional
@WithMockUser(username = "john.doe@test.com")
class ApiTransactionControllerTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private User userJaneDoe = HelperTest.janeDoe();
    private Money testMoney = HelperTest.money();
    private final TransactionDTOBuilder transaction =
            TransactionDTO.builder().change(HelperTest.money()).description("Description");

    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    private final MockHttpServletRequestBuilder fromAppToBankBuilder =
            put("/api/transaction/fromAppToBank")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder fromBankToAppBuilder =
            put("/api/transaction/fromBankToApp")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder fromUserToUserBuilder =
            put("/api/transaction/fromUserToUser")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

    // -- fromAppToBank --

    @Test
    @WithAnonymousUser
    void fromAppToBankNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromAppToBankBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(testMoney)))
                .andExpectRedirectAuth();
    }

    @Test
    void fromAppToBankNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromAppToBankBuilder.content(json.writeValueAsString(testMoney)))
                .andExpect(status().isForbidden());
    }

    @Test
    void fromAppToBankNoBody() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromAppToBankBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void fromAppToBankNotEnoughCash() throws Exception {
        userRepository.save(userJohnDoe);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(
                        new EErrorTransaction().new BalanceTooLow(userJohnDoe, testMoney)))
                .build();

        final var actual = mockMvc.perform(fromAppToBankBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(testMoney)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void fromAppToBankSuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);
        final var expected = BankTransaction.builder()
                .amount(testMoney)
                .sens(ESens.FROM_APP_TO_BANK)
                .user(userJohnDoe)
                .build();

        final var actualJson = mockMvc.perform(fromAppToBankBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(testMoney)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = json.readValue(actualJson, BankTransaction.class);
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
        assertEquals(
                userJohnDoe.getBalance(),
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect());
    }

    // -- fromBankToApp --

    @Test
    @WithAnonymousUser
    void fromBankToAppNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromBankToAppBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(testMoney)))
                .andExpectRedirectAuth();
    }

    @Test
    void fromBankToAppNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromBankToAppBuilder.content(json.writeValueAsString(testMoney)))
                .andExpect(status().isForbidden());
    }

    @Test
    void fromBankToAppNoBody() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromBankToAppBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void fromBankToAppSuccess() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);
        final var expected = BankTransaction.builder()
                .amount(testMoney)
                .sens(ESens.FROM_BANK_TO_APP)
                .user(userJohnDoe)
                .build();

        final var actualJson = mockMvc.perform(fromBankToAppBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(testMoney)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = json.readValue(actualJson, BankTransaction.class);
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
        assertEquals(userJohnDoe.getBalance(), testMoney);
    }

    // -- fromUserToUser --

    @Test
    @WithAnonymousUser
    void fromUserToUserNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromUserToUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(transaction
                                .maybeReceiver(userJaneDoe.getEmail())
                                .build())))
                .andExpectRedirectAuth();
    }

    @Test
    void fromUserToUserNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromUserToUserBuilder.content(json.writeValueAsString(
                        transaction.maybeReceiver(userJaneDoe.getEmail()).build())))
                .andExpect(status().isForbidden());
    }

    @Test
    void fromUserToUserNoBody() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        userJohnDoe.setBalance(testMoney);

        mockMvc.perform(fromUserToUserBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void fromUserToUserDoesNotExist() throws Exception {
        userRepository.save(userJohnDoe);
        userJohnDoe.setBalance(testMoney);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud()
                .new Exist(userJaneDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist())))
                .build();

        final var actual = mockMvc.perform(fromUserToUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(transaction
                                .maybeReceiver(userJaneDoe.getEmail())
                                .build())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void fromUserToUserEqualId() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);

        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorTransaction().new EqualId(userJohnDoe)))
                .build();
        final var actual = mockMvc.perform(fromUserToUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(transaction
                                .maybeReceiver(userJohnDoe.getEmail())
                                .build())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void fromUserToUserBalanceTooLow() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);

        final var expected = ResultDTO.builder()
                .result(Result.ofErr(
                        new EErrorTransaction().new BalanceTooLow(userJohnDoe, testMoney)))
                .build();
        final var actual = mockMvc.perform(fromUserToUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(transaction
                                .maybeReceiver(userJaneDoe.getEmail())
                                .build())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void fromUserToUserSuccess() throws Exception {
        userJohnDoe.setBalance(testMoney);
        userJohnDoe = userRepository.save(userJohnDoe);
        userJaneDoe = userRepository.save(userJaneDoe);

        final var expected = Transaction.builder()
                .amount(testMoney)
                .description(transaction
                        .maybeReceiver(userJaneDoe.getEmail())
                        .build()
                        .getDescription())
                .sender(userJohnDoe)
                .receiver(userJaneDoe)
                .build();
        final var expectedJohnDoe =
                Money.builder().uint(BigDecimal.valueOf(0, 2)).tryBuild().expect();
        final var expectedJaneDoe = feeService.calculateChangeMinusFee(testMoney);

        final var actualJson = mockMvc.perform(fromUserToUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(transaction
                                .maybeReceiver(userJaneDoe.getEmail())
                                .build())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = json.readValue(actualJson, Transaction.class);
        expected.setId(actual.getId());
        expected.setDate(actual.getDate());

        assertEquals(expected, actual);
        assertEquals(expectedJohnDoe, userJohnDoe.getBalance());
        assertEquals(expectedJaneDoe, userJaneDoe.getBalance());
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @NonNull private final UserRepository userRepository;

    @NonNull private final FeeService feeService;

    @Autowired
    ApiTransactionControllerTest(
            @NonNull final MockMvc mockMvc,
            @NonNull final UserRepository userRepository,
            @NonNull final FeeService feeService) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.feeService = feeService;
    }
}
