package com.example.bankapp.auth;

import com.example.bankapp.account.AccountService;
import com.example.bankapp.common.ApiException;
import com.example.bankapp.security.JwtService;
import com.example.bankapp.user.User;
import com.example.bankapp.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request){
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)){
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = userRepository.save(User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build());

        accountService.createAccountFor(user);

        return new AuthResponse(jwtService.generateToken(email));
    }

    public AuthResponse login(LoginRequest request){
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        return new AuthResponse(jwtService.generateToken(email));
    }
}
