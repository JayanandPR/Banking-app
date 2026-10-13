package com.example.bankapp.transaction;

import com.example.bankapp.account.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void record(Account account, TransactionType type, BigDecimal amount, String description){
        transactionRepository.save(Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .description(description)
                .build()
        );
    }

    @Transactional()
    public List<TransactionResponse> getHistory(String email){
        return transactionRepository.findByAccountUserEmailOrderByCreatedAtDesc(email).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
