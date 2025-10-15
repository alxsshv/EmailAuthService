package com.alxsshv.security;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.service.AccountService;
import com.alxsshv.service.AuthPairService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class CodeAuthenticationProvider  implements AuthenticationProvider {

    private final AccountService accountService;

    private final AuthPairService authPairService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        String email = (String) authentication.getPrincipal();
        String code = authentication.getCredentials().toString();
        AuthPair authPair = authPairService.getByEmail(email);
        AccountDetails accountDetails = authenticateByCode(authPair, code);
        return new UsernamePasswordAuthenticationToken(accountDetails, null, accountDetails.getAuthorities());
    }

    private AccountDetails authenticateByCode(AuthPair authPair, String code) {
        if (!authPair.getCode().equals(code)) {
            throw new BadCredentialsException("Code is invalid or expired");
        }
        Account account = accountService.getAccountByEmail(authPair.getEmail());
        return new AccountDetails(account);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
