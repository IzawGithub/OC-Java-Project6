package com.paymybuddy.mvp.controller;

import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;
import com.paymybuddy.mvp.model.dto.ResultDTO;

import net.xyzsd.dichotomy.Result;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler({EErrorPayMyBuddy.class, HttpMessageNotReadableException.class})
    public @NonNull ResponseEntity<ResultDTO<Void>> handleError(
            @NonNull final EErrorPayMyBuddy error) {
        return ResultDTO.<Void>builder().result(Result.ofErr(error)).build().toResponseEntity();
    }

    @ExceptionHandler({NoResourceFoundException.class, AccessDeniedException.class})
    public @NonNull ModelAndView handle403And404Error() {

        return new ModelAndView("redirect:/");
    }
}
