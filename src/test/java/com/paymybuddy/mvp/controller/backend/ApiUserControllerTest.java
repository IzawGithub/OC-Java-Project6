package com.paymybuddy.mvp.controller.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.errors.EErrorCrud;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.ResultDTO;
import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.model.dto.UserUpdateDTO;
import com.paymybuddy.mvp.repository.UserRepository;

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

@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
@ExtensionMethod({HelperTest.class})
@SpringBootTest
@Transactional
@WithMockUser(username = "john.doe@test.com")
// TODO: WebMvcTest integration without mock
class ApiUserControllerTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private UserDTO userJohnDoeDto = HelperTest.johnDoeDTO();
    private User userJaneDoe = HelperTest.janeDoe();
    private UserDTO userJaneDoeDto = HelperTest.janeDoeDTO();
    private final ObjectMapper json = new ObjectMapper();

    private final MockHttpServletRequestBuilder createUserBuilder = post("/api/user")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder createBuddyBuilder = post("/api/user/buddy")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder readUserBuilder = get("/api/user")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder updateUserBuilder = put("/api/user")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    private final MockHttpServletRequestBuilder deleteUserBuilder = delete("/api/user")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

    // -- createUser--

    @Test
    void createUserAuthenticated() throws Exception {
        mockMvc.perform(createUserBuilder.content(json.writeValueAsString(userJohnDoeDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void createUserNoCsrf() throws Exception {
        mockMvc.perform(createUserBuilder.content(json.writeValueAsString(userJohnDoeDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void createUserNoBody() throws Exception {
        mockMvc.perform(createUserBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void createUserAlreadyExist() throws Exception {
        userRepository.save(userJohnDoe);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud()
                .new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.Exist())))
                .build();
        final var actual = mockMvc.perform(createUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJohnDoeDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    @WithAnonymousUser
    void createUserSuccess() throws Exception {
        final var expected = userJohnDoe;
        mockMvc.perform(createUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJohnDoeDto)))
                .andExpect(status().isOk());

        // The DTO object cannot set it's own ID
        final var actual = userRepository.findByEmail(userJohnDoe.getEmail()).get();
        expected.setId(actual.getId());

        assertEquals(expected, actual);
    }

    // -- createBuddy --

    @Test
    @WithAnonymousUser
    void createBuddyNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        mockMvc.perform(createBuddyBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJaneDoe.getEmail())))
                .andExpectRedirectAuth();
    }

    @Test
    void createBuddyNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(createBuddyBuilder.content(json.writeValueAsString(userJohnDoeDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBuddyNoBody() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(createBuddyBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void createBuddyDoesNotExist() throws Exception {
        userRepository.save(userJohnDoe);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud()
                .new Exist(userJaneDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist())))
                .build();

        final var actual = mockMvc.perform(createBuddyBuilder
                .with(csrf())
                .content(json.writeValueAsString(userJaneDoe.getEmail())));

        assertEquals(
                json.writeValueAsString(expected),
                actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void createBuddyEqualsValues() throws Exception {
        userRepository.save(userJohnDoe);
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud().new EqualsValues(userJohnDoe.getEmail())))
                .build();

        final var actual = mockMvc.perform(createBuddyBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJohnDoe.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void createBuddySuccess() throws Exception {
        userJohnDoe = userRepository.save(userJohnDoe);
        final var expected = userRepository.save(userJaneDoe);

        mockMvc.perform(createBuddyBuilder
                .with(csrf())
                .content(json.writeValueAsString(expected.getEmail())));

        final var actual = userJohnDoe.getBuddies().getFirst();
        assertEquals(expected, actual);
    }

    // -- readUser --

    @Test
    @WithAnonymousUser
    void readUserNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(readUserBuilder.with(csrf())).andExpectRedirectAuth();
    }

    @Test
    void readUserSuccess() throws Exception {
        final var expected = userRepository.save(userJohnDoe);
        // No need for CSRF, it's a GET request so there's no change of state
        final var actualJson =
                mockMvc.perform(readUserBuilder).andReturn().getResponse().getContentAsString();
        final var actual = json.readValue(actualJson, User.class);

        assertEquals(expected, actual);
    }

    // -- updateUser --

    @Test
    @WithAnonymousUser
    void updateUserNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        mockMvc.perform(updateUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJaneDoeDto)))
                .andExpectRedirectAuth();
    }

    @Test
    void updateUserNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        userRepository.save(userJaneDoe);
        mockMvc.perform(updateUserBuilder.content(json.writeValueAsString(userJaneDoeDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserNoBody() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(updateUserBuilder.with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void updateUserDoesNotExist() throws Exception {
        final var expected = ResultDTO.builder()
                .result(Result.ofErr(new EErrorCrud()
                .new Exist(userJohnDoe.getEmail(), new EErrorCrud.EExist.DoesNotExist())))
                .build();
        final var actual = mockMvc.perform(updateUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJaneDoeDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(json.writeValueAsString(expected), actual);
    }

    @Test
    void updateUserSuccessNoFields() throws Exception {
        final var expected = userRepository.save(userJohnDoe);
        final var actualJson = mockMvc.perform(updateUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(UserUpdateDTO.builder().build())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = json.readValue(actualJson, User.class);

        assertEquals(expected, actual);
    }

    @Test
    void updateUserSuccessAllFields() throws Exception {
        final var expected = userRepository.save(userJohnDoe);
        final var actualJson = mockMvc.perform(updateUserBuilder
                        .with(csrf())
                        .content(json.writeValueAsString(userJaneDoeDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = json.readValue(actualJson, User.class);

        assertEquals(expected, actual);
    }

    // -- deleteUser --

    @Test
    @WithAnonymousUser
    void deleteUserNotAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(deleteUserBuilder.with(csrf())).andExpectRedirectAuth();
    }

    @Test
    void deleteUserNoCsrf() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(deleteUserBuilder).andExpect(status().isForbidden());
    }

    @Test
    void deleteUserSuccess() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(deleteUserBuilder.with(csrf()));

        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isEmpty());
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @NonNull private final UserRepository userRepository;

    @Autowired
    ApiUserControllerTest(
            @NonNull final MockMvc mockMvc, @NonNull final UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }
}
