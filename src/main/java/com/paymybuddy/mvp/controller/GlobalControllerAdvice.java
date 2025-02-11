package com.paymybuddy.mvp.controller;

import com.paymybuddy.mvp.controller.frontend.UserController.Navbar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Value("${spring.application.name}")
    private @NonNull String applicationName;

    @ModelAttribute("applicationName")
    public @NonNull String getApplicationName() {
        return applicationName;
    }

    @ModelAttribute("navbarItems")
    public @NonNull List<Navbar> getNavbarItems() {
        return Arrays.asList(Navbar.values());
    }
}
