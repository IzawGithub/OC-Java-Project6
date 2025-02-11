package com.paymybuddy.mvp.controller.frontend;

import lombok.AllArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@AllArgsConstructor
@Controller
@RequestMapping("/auth")
@PreAuthorize("isAnonymous()")
public class AuthController {
    @GetMapping
    public @NonNull ModelAndView home(@AuthenticationPrincipal final UserDetails userAuth) {
        return new ModelAndView("/auth/index.html");
    }
}
