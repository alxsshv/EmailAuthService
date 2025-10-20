package com.alxsshv.service.impl;

import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.exception.AuthenticationProcessingException;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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

    private final AuthenticationManager authenticationManager;

    private final AccountService accountService;

    private final AuthPairService authPairService;

    private final CodeSendingService codeSendingService;

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
        codeSendingService.sendCode(authPair);
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.code()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Account account = accountService.getAccountByEmail(authentication.getName());
            activateAccountIfDisable(account);
            AuthResponse authResponse = buildAuthResponse(account);
            authPairService.deleteAllPairsForAccount(account);
            return authResponse;
        } catch (EntityNotFoundException ex) {
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
