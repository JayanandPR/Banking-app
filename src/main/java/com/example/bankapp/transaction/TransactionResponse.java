package com.example.bankapp.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction t){
        return new TransactionResponse(
                t.getType(),
                t.getAmount(),
                t.getDescription(),
                t.getCreatedAt()
        );
    }
}
