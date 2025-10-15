package com.alxsshv.controller;

import com.alxsshv.dto.AccountDto;
import com.alxsshv.dto.mapper.AccountMapper;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountMapper accountMapper;

    private final AccountService accountService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('READ_ONLY')")
    public List<AccountDto> getAllAccounts(@AuthenticationPrincipal Jwt jwt) {
        System.out.println(jwt.getSubject());
        List<Account> accounts = accountService.getAllAccounts();
        return accountMapper.map(accounts);
    }



}
