package com.paymybuddy.mvp.controller.frontend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.paymybuddy.mvp.HelperTest;

import lombok.experimental.ExtensionMethod;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class HomeControllerTest {
    private final MockHttpServletRequestBuilder getHome = get("/");

    @Test
    @WithAnonymousUser
    void redirectAnonymousToAuth() throws Exception {
        mockMvc.perform(getHome).andExpectRedirect("/auth");
    }

    @Test
    @WithMockUser
    void redirectUserToUser() throws Exception {
        mockMvc.perform(getHome).andExpectRedirect("/user");
    }

    // -- Beans --

    @NonNull private final MockMvc mockMvc;

    @Autowired
    HomeControllerTest(@NonNull final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
}
