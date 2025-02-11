package com.paymybuddy.mvp.controller.frontend;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {
    @Getter
    @AllArgsConstructor
    public enum Navbar {
        BANK("/user/bank", RequestMethod.GET),
        BUDDIES("/user/buddy", RequestMethod.GET),
        PROFIL("/user/profil", RequestMethod.GET),
        DISCONNECT("/auth/log-out", RequestMethod.POST);

        @NonNull private final String path;

        @NonNull private final RequestMethod method;
    }
}
