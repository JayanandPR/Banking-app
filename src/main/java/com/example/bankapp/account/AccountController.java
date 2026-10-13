package com.example.bankapp.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public AccountResponse myAccount(Principal principal){
        return accountService.getAccount(principal.getName());
    }

    @PostMapping("/deposit")
    public AccountResponse deposit(Principal principal, @Valid @RequestBody AmountRequest request){
        return accountService.deposit(principal.getName(), request.amount());
    }

    @PostMapping("/withdraw")
    public AccountResponse withdraw(Principal principal, @Valid @RequestBody AmountRequest request){
        return accountService.withdraw(principal.getName(), request.amount());
    }

    @PostMapping("/transfer")
    public AccountResponse transfer(Principal principal, @Valid @RequestBody TransferRequest request){
        return accountService.transfer(principal.getName(), request);
    }
}
