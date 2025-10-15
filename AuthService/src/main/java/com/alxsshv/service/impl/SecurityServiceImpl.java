package com.alxsshv.service.impl;

import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.exception.AuthenticationProcessingException;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.security.AccountDetails;
import com.alxsshv.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final AccountService accountService;

    private final AuthPairService authPairService;

    private final CodeDistributionService codeDistributionService;

    private final JwtService jwtService;

    /** Метод входа в сервис. Если аккаунта с указанным email нет он автоматически создаётся */
    @Override
    @Transactional
    public void getAuthorizationCode(String email) {
        Account account;
        try {
            account = accountService.getAccountByEmail(email);
        } catch (EntityNotFoundException ex) {
            account = createAccount(email);
        }
        AuthPair authPair = authPairService.createAndSaveAuthPair(account);
        codeDistributionService.sendCode(authPair);
    }

    private Account createAccount(String email) {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .authorities(Set.of(Authorities.READ_ONLY))
                .status(Status.DISABLED)
                .build();
       return accountService.addAccount(account);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Set<AuthPair> authPairSet = authPairService.getAllByEmail(request.email());
            validateCode(authPairSet, request.code());
            Account account = accountService.getAccountByEmail(request.email());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(account.getEmail(), null, account.getAuthorities()));
            activateAccountIfDisable(account);
            AuthResponse authResponse = buildAuthResponse(account);
            authPairService.deleteAllPairsForAccount(account);
            return authResponse;
        } catch (EntityNotFoundException ex) {
            throw new AuthenticationProcessingException("Ошибка аутентификации. Код не действителен или просрочен");
        }
    }


    private void  validateCode(Set<AuthPair> authPairSet, String code) {
        boolean isValidCode = authPairSet.stream()
                .map(AuthPair::getCode)
                .anyMatch(authPairCode -> authPairCode.equals(code));
        if (!isValidCode) {
            throw new AuthenticationProcessingException("Ошибка аутентификации. Код не действителен или просрочен");
        }
    }

    private void activateAccountIfDisable(Account account) {
        if (account.getStatus().equals(Status.DISABLED)) {
            accountService.activateAccount(account.getId());
        }
    }

    private AuthResponse buildAuthResponse(Account account) {
        String accessToken = jwtService.generateAccessToken(account);
        return new AuthResponse(account.getEmail(), accessToken);
    }



}
