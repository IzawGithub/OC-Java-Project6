package com.paymybuddy.mvp.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties({"cause", "localizedMessage", "message", "stackTrace", "suppressed"})
public sealed class EErrorPayMyBuddy extends Exception permits EErrorEmail, EErrorMoney {}
