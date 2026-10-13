package com.example.bankapp.account;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        String ownerName,
        String email,
        BigDecimal balance
) {
    public static AccountResponse from(Account account){
        return new AccountResponse(
                account.getAccountNumber(),
                account.getUser().getName(),
                account.getUser().getEmail(),
                account.getBalance()
        );
    }
}
