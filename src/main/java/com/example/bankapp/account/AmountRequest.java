package com.example.bankapp.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AmountRequest(
        @NotNull
        @DecimalMin(value = "0.01", message = "must be greater than 0")
        @Digits(integer = 17, fraction = 2, message = "must have at most 2 decimal places")
        BigDecimal amount
) {
}
