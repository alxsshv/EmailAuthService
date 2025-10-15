package com.alxsshv.controller;

import com.alxsshv.dto.AccountDto;
import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.dto.ServiceMessage;
import com.alxsshv.dto.mapper.AccountMapper;
import com.alxsshv.service.AccountService;
import com.alxsshv.service.SecurityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AccountMapper userMapper;

    private final AccountService accountService;

    private final SecurityService securityService;


    @PostMapping("/code")
    public ResponseEntity<ServiceMessage> getAuthorizationCode(@RequestParam @NotBlank String email) {
        System.out.println("Адрес электронной почты: " + email);
        securityService.getAuthorizationCode(email);
        return ResponseEntity.ok(new ServiceMessage("The authorization code has been sent. Please check your email."));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        AuthResponse response = securityService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<AccountDto> getCurrentUser() {
        AccountDto userDto = userMapper.map(accountService.getAccountByEmail("test@mail.com"));
        return ResponseEntity.ok(userDto);
    }





}
