package com.paymybuddy.mvp;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.paymybuddy.mvp.model.User;
import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.model.internal.Secret;

import net.xyzsd.dichotomy.Maybe;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;

public final class HelperTest {
    private static final String JOHN_DOE_PASSWORD = "password";
    private static final String JANE_DOE_PASSWORD = "Jane Doe password";

    private static final Pattern REGEX_CSRF =
            Pattern.compile("(?<begin><input .* name=\"_csrf\" value=\")(?<value>.*)(?<end>\"/>)");
    private static final Pattern REGEX_ARGON2 = Pattern.compile("\\$argon2.*");

    public static User johnDoe() {
        return User.builder()
                .id(UUID.fromString("01234567-abcd-1abc-abcd-0123456789ab"))
                .email(Email.builder().email("john.doe@test.com").tryBuild().expect())
                .username("John Doe")
                .password(new Secret(JOHN_DOE_PASSWORD))
                .build();
    }

    public static UserDTO johnDoeDTO() {
        final var user = johnDoe();
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(JOHN_DOE_PASSWORD)
                .build();
    }

    public static User janeDoe() {
        return User.builder()
                .id(UUID.fromString("76543210-dcba-cba1-dcba-ba9876543210"))
                .email(Email.builder().email("jane.doe@test.com").tryBuild().expect())
                .username("Jane Doe")
                .password(new Secret(JANE_DOE_PASSWORD))
                .build();
    }

    public static UserDTO janeDoeDTO() {
        final var user = janeDoe();
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(JANE_DOE_PASSWORD)
                .build();
    }

    public static Money money() {
        return Money.builder().uint(new BigDecimal("13.37")).tryBuild().expect();
    }

    public static String sanitizedHtml(@NonNull final String html) {
        var sanitisedHtml = html;
        final var matcherCsrf = REGEX_CSRF.matcher(sanitisedHtml);
        if (matcherCsrf.find()) {
            sanitisedHtml = matcherCsrf.replaceAll("${begin}${end}");
        }
        final var matcherArgon2 = REGEX_ARGON2.matcher(sanitisedHtml);
        if (matcherArgon2.find()) {
            sanitisedHtml = matcherArgon2.replaceAll("");
        }
        return sanitisedHtml;
    }

    public static Maybe<Authentication> expectAuth(@NonNull final ResultActions mvc)
            throws Exception {
        return Maybe.ofNullable(
                        (SecurityContextImpl)
                                andExpectRedirectAuth(mvc)
                                        .andReturn()
                                        .getRequest()
                                        .getAttribute(
                                                "org.springframework.security.web.context.RequestAttributeSecurityContextRepository.SPRING_SECURITY_CONTEXT"))
                .flatMap(securityContext -> Maybe.ofNullable(securityContext.getAuthentication()));
    }

    // -- lombok.ExtensionMethod --

    public static ResultActions andExpectRedirectAuth(@NonNull final ResultActions mvc)
            throws Exception {
        return andExpectRedirect(mvc, "/");
    }

    public static ResultActions andExpectRedirect(
            @NonNull final ResultActions mvc, @NonNull final String redirectUrl) throws Exception {
        return mvc.andExpectAll(status().isFound(), redirectedUrl(redirectUrl));
    }
}
