package com.paymybuddy.mvp.controller.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorAuth;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.ResultDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.repository.UserRepository;

import jakarta.transaction.Transactional;

import net.xyzsd.dichotomy.Result;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ApiAuthControllerTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private final ObjectMapper json = new ObjectMapper();

    private final MockHttpServletRequestBuilder readAuth = get("/api/auth")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

    @Test
    @WithMockUser(username = "wrong.email@test.com")
    void authToUserWrongEmail() throws Exception {
        userRepository.save(userJohnDoe);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud()
                .new Exist(
                        Email.builder().email("wrong.email@test.com").tryBuild().expect(),
                        new EErrorCrud.EExist.DoesNotExist())))
                .build();
        final var actual = mockMvc.perform(readAuth).andReturn().getResponse().getContentAsString();
        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    @WithMockUser(username = "john.doe@test.com", password = "0xDEADBEEF")
    void authToUserWrongPassword() throws Exception {
        userRepository.save(userJohnDoe);

        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorAuth().new InvalidUsernameOrPassword()))
                .build();
        final var actual = mockMvc.perform(readAuth).andReturn().getResponse().getContentAsString();
        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void authToUserSuccess() throws Exception {
        final var expected = userRepository.save(userJohnDoe);
        final var actualJson =
                mockMvc.perform(readAuth).andReturn().getResponse().getContentAsString();
        final var actual = json.readValue(actualJson, User.class);

        assertEquals(expected, actual);
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @NonNull private final UserRepository userRepository;

    @Autowired
    ApiAuthControllerTest(
            @NonNull final MockMvc mockMvc,
            @NonNull final UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }
}
