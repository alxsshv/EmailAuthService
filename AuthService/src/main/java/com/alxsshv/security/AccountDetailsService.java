package com.alxsshv.security;

import com.alxsshv.entity.Account;
import com.alxsshv.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountService.getAccountByEmail(email);
        System.out.println(account.getAuthorities().stream().findFirst());
        return new AccountDetails(account);
    }
}
