package com.paymybuddy.mvp.controller.frontend;

import com.paymybuddy.mvp.model.dto.UserDTO;
import com.paymybuddy.mvp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import net.xyzsd.dichotomy.Result.Err;
import net.xyzsd.dichotomy.Result.OK;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
@RequestMapping("/auth")
@PreAuthorize("isAnonymous()")
public class AuthController {
    @GetMapping
    public @NonNull ModelAndView home(@AuthenticationPrincipal final UserDetails userAuth) {
        return new ModelAndView("/auth/index.html");
    }

    @GetMapping("/log-in")
    public @NonNull ModelAndView logIn(@AuthenticationPrincipal final UserDetails userAuth) {
        return new ModelAndView();
    }

    @GetMapping("/sign-up")
    public @NonNull ModelAndView signUp(@AuthenticationPrincipal final UserDetails userAuth) {
        return new ModelAndView();
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @NonNull ModelAndView trySignUp(
            @NonNull final HttpServletRequest request,
            @NonNull final UserDTO userDTO,
            @NonNull final RedirectAttributes redirectAttributes) {
        final var result = userService.tryCreateUser(userDTO.toUser());
        return switch (result) {
            case OK(final var ok) -> {
                // Manual auth after creating the user, this way we can redirect directly to the user profil
                final var authUser =
                new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword());

                final var securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authManager.authenticate(authUser));
                final var session = request.getSession(true);
                session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
                yield new ModelAndView("redirect:/user");
            }
            case Err(final var err) -> {
                redirectAttributes.addFlashAttribute("error", err);
                yield new ModelAndView("redirect:/auth/sign-up");
            }
        };
    }

    // -- Beans --

    @NonNull private UserService userService;

    @NonNull private AuthenticationManager authManager;
}
