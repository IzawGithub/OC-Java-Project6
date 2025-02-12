package com.paymybuddy.mvp.controller.frontend;

import static com.diffplug.selfie.Selfie.expectSelfie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.repository.UserRepository;

import jakarta.transaction.Transactional;

import lombok.experimental.ExtensionMethod;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
@WithAnonymousUser
class AuthControllerTest {
    private User userJohnDoe = HelperTest.johnDoe();
    private UserDTO userJohnDoeDTO = HelperTest.johnDoeDTO();

    private final MockHttpServletRequestBuilder getAuth = get("/auth");
    private final MockHttpServletRequestBuilder getLogin = get("/auth/log-in");
    private final MockHttpServletRequestBuilder getSignup = get("/auth/sign-up");
    private final MockHttpServletRequestBuilder postSignup =
            post("/auth/sign-up").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void authAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(getAuth).andExpectRedirectAuth();
    }

    @Test
    void auth() throws Exception {
        final var actual = mockMvc.perform(getAuth).andReturn().getResponse().getContentAsString();
        expectSelfie(actual).toMatchDisk();
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void getLoginAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(getLogin).andExpectRedirectAuth();
    }

    @Test
    void getLogin() throws Exception {
        final var actualWithCsrf =
                mockMvc.perform(getLogin).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualWithCsrf);

        expectSelfie(actual).toMatchDisk();
    }

    @Test
    void logInWrongEmail() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/auth/log-in")
                        .user("email", "0xDEADBEEF")
                        .password("password"))
                .andExpectRedirect("/auth/log-in?error");
    }

    @Test
    void logInWrongPassword() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/auth/log-in")
                        .user("email", userJohnDoe.getEmail().toString())
                        .password("0xDEADBEEF"))
                .andExpectRedirect("/auth/log-in?error");
    }

    @Test
    void logInSuccess() throws Exception {
        userRepository.save(userJohnDoe);
        final var expectedEmail = userJohnDoe.getEmail();
        final var expectedPassword = userJohnDoe.getPassword();

        final var auth = mockMvc.perform(formLogin()
                        .loginProcessingUrl("/auth/log-in")
                        .user("email", userJohnDoe.getEmail().toString())
                        .password("password"))
                .expectAuth()
                .expect();

        final var actual = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        final var actualEmail = actual.getUsername();
        final var actualPassword = actual.getPassword();
        assertTrue(auth.isAuthenticated());
        assertEquals(expectedEmail.toString(), actualEmail);
        assertEquals(expectedPassword.toString(), actualPassword);
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void logOutSuccess() throws Exception {
        userRepository.save(userJohnDoe);
        final var auth = mockMvc.perform(logout("/auth/log-out")).expectAuth();
        assertTrue(auth.isNone());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void getSignUpAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(getSignup).andExpectRedirectAuth();
    }

    @Test
    void getSignUp() throws Exception {
        final var actualWithCsrf =
                mockMvc.perform(getSignup).andReturn().getResponse().getContentAsString();
        final var actual = HelperTest.sanitizedHtml(actualWithCsrf);

        expectSelfie(actual).toMatchDisk();
    }

    MockHttpServletRequestBuilder postSignUpParam(@NonNull final UserDTO userDTO) {
        return postSignup
                .param("username", userDTO.getUsername())
                .param("email", userDTO.getEmail().toString())
                .param("password", userDTO.getPassword());
    }

    @Test
    @WithMockUser(username = "john.doe@test.com")
    void postSignUpAuthenticated() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(postSignUpParam(userJohnDoeDTO).with(csrf()))
                .andExpect(result -> assertInstanceOf(
                        AuthorizationDeniedException.class, result.getResolvedException()))
                .andExpectRedirectAuth();
    }

    @Test
    void postSignUpUserAlreadyExist() throws Exception {
        userRepository.save(userJohnDoe);
        mockMvc.perform(postSignUpParam(userJohnDoeDTO).with(csrf()))
                .andExpectRedirect("/auth/sign-up");
    }

    @Test
    void postSignUp() throws Exception {
        mockMvc.perform(postSignup
                        .with(csrf())
                        .param("username", userJohnDoeDTO.getUsername())
                        .param("email", userJohnDoeDTO.getEmail().toString())
                        .param("password", userJohnDoeDTO.getPassword()))
                .andExpectRedirect("/user");
        assertTrue(userRepository.findByEmail(userJohnDoe.getEmail()).isPresent());
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @NonNull private final UserRepository userRepository;

    @Autowired
    AuthControllerTest(
            @NonNull final MockMvc mockMvc, @NonNull final UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }
}
