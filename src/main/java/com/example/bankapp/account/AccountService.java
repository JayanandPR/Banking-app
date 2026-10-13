package com.example.bankapp.account;

import com.example.bankapp.common.ApiException;
import com.example.bankapp.transaction.TransactionService;
import com.example.bankapp.transaction.TransactionType;
import com.example.bankapp.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Transactional
    public Account createAccountFor(User user){
        return accountRepository.save(Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public AccountResponse getAccount(String email){
        return AccountResponse.from(findByEmail(email));
    }

    @Transactional
    public AccountResponse deposit(String email, BigDecimal amount){
        Account account = lock(findByEmail(email).getAccountNumber());

        account.setBalance(account.getBalance().add(amount));
        transactionService.record(account, TransactionType.DEPOSIT, amount, "Deposit");

        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse withdraw(String email, BigDecimal amount){
        Account account = lock(findByEmail(email).getAccountNumber());
        ensureSufficientFunds(account, amount);

        account.setBalance(account.getBalance().subtract(amount));

        transactionService.record(account, TransactionType.WITHDRAWAL, amount, "Withdrawal");

        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse transfer(String email, TransferRequest request){
        String fromNumber = findByEmail(email).getAccountNumber();
        String toNumber = request.toAccountNumber();

        if (fromNumber.equals(toNumber)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot transfer to your own account");
        }

        boolean fromFirst = fromNumber.compareTo(toNumber) < 0;
        Account first = lock(fromFirst ? fromNumber : toNumber);
        Account second = lock(fromFirst ? toNumber : fromNumber);
        Account from = fromFirst ? first : second;
        Account to = fromFirst ? second : first;

        ensureSufficientFunds(from, request.amount());

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        transactionService.record(from, TransactionType.TRANSFER_OUT, request.amount(),
                "Transfer to " + to.getAccountNumber());

        transactionService.record(to, TransactionType.TRANSFER_IN, request.amount(),
                "Transfer from " + from.getAccountNumber());

        return AccountResponse.from(from);
    }


//    ------ helpers ------

    private Account findByEmail(String email){
        return accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not Found"));
    }

    private Account lock(String accountNumber){
        return accountRepository.findWithLockByAccountNumber(accountNumber)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account " + accountNumber + " not found"));
    }

    private void ensureSufficientFunds(Account account, BigDecimal amount){
        if (account.getBalance().compareTo(amount) < 0){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient Balance");
        }
    }

    private String generateAccountNumber(){
        String number;
        do {
            number = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}

