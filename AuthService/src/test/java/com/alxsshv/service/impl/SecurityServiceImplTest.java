package com.alxsshv.service.impl;

import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.security.AccountDetails;
import com.alxsshv.service.AccountService;
import com.alxsshv.service.AuthPairService;
import com.alxsshv.service.CodeSendingService;
import com.alxsshv.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthPairService authPairService;

    @Mock
    private CodeSendingService codeSendingService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    @DisplayName("Test getAuthorizationCode method when email already exists then send create authPair and send authorization dode")
    void testGetAuthorizationCode_whenEmailAlreadyExists_thenSendCode() {
        String email = "test@email.com";
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        AuthPair pair = AuthPair.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .code("123456")
                .expirationInSeconds(200L)
                .build();

        Mockito.when(accountService.getAccountByEmail(email)).thenReturn(account);
        Mockito.when(authPairService.createAndSaveAuthPair(account)).thenReturn(pair);

        securityService.getAuthorizationCode(email);

        Mockito.verify(accountService, Mockito.times(1)).getAccountByEmail(email);
        Mockito.verify(authPairService, Mockito.times(1)).createAndSaveAuthPair(account);
        Mockito.verify(accountService, Mockito.never()).addAccount(account);
        Mockito.verify(codeSendingService, Mockito.times(1)).sendCode(pair);
    }

    @Test
    @DisplayName("Test getAuthorizationCode method when account with email is not exists then send create authPair and send authorization dode")
    void testGetAuthorizationCode_whenEmailIsNotExists_thenSendCode() {
        String email = "test@email.com";
        AuthPair pair = AuthPair.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .code("123456")
                .expirationInSeconds(200L)
                .build();

        Mockito.when(accountService.getAccountByEmail(email)).thenThrow(EntityNotFoundException.class);
        Mockito.when(accountService.addAccount(any(Account.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Mockito.when(authPairService.createAndSaveAuthPair(any(Account.class))).thenReturn(pair);

        securityService.getAuthorizationCode(email);

        Mockito.verify(accountService, Mockito.times(1)).getAccountByEmail(email);
        Mockito.verify(authPairService, Mockito.times(1)).createAndSaveAuthPair(any(Account.class));
        Mockito.verify(accountService, Mockito.times(1)).addAccount(any(Account.class));
        Mockito.verify(codeSendingService, Mockito.times(1)).sendCode(pair);
    }


    @Test
    @DisplayName("Test authenticate method when account is enabled then return AuthResponse")
    void testAuthenticate_whenAccountIsEnabled_thenReturnAuthResponse() {
        String email = "test@email.com";
        String code = "123456";
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        String token = "token string";
        AuthResponse expectedAuthResponse = new AuthResponse(email, token);
        AuthRequest authRequest = new AuthRequest(email, code);
        Authentication authentication = new UsernamePasswordAuthenticationToken(new AccountDetails(account), null, account.getAuthorities());

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(accountService.getAccountByEmail(email)).thenReturn(account);
        Mockito.when(jwtService.generateAccessToken(account)).thenReturn(token);

        AuthResponse actualAuthResponse = securityService.authenticate(authRequest);

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(any());
        Mockito.verify(accountService, Mockito.times(1)).getAccountByEmail(email);
        Mockito.verify(accountService, Mockito.never()).activateAccount(account.getId());
        Mockito.verify(jwtService, Mockito.times(1)).generateAccessToken(account);
        Mockito.verify(authPairService, Mockito.times(1)).deleteAllPairsForAccount(account);
        Assertions.assertEquals(expectedAuthResponse, actualAuthResponse);
    }

    @Test
    @DisplayName("Test authenticate method when account is disabled then return AuthResponse")
    void testAuthenticate_whenAccountIsDisabled_thenReturnAuthResponse() {
        String email = "test@email.com";
        String code = "123456";
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .status(Status.DISABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        String token = "token string";
        AuthResponse expectedAuthResponse = new AuthResponse(email, token);
        AuthRequest authRequest = new AuthRequest(email, code);
        Authentication authentication = new UsernamePasswordAuthenticationToken(new AccountDetails(account), null, account.getAuthorities());

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);
        Mockito.when(accountService.getAccountByEmail(email)).thenReturn(account);
        Mockito.when(jwtService.generateAccessToken(account)).thenReturn(token);

        AuthResponse actualAuthResponse = securityService.authenticate(authRequest);

        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(any());
        Mockito.verify(accountService, Mockito.times(1)).getAccountByEmail(email);
        Mockito.verify(accountService, Mockito.times(1)).activateAccount(account.getId());
        Mockito.verify(jwtService, Mockito.times(1)).generateAccessToken(account);
        Mockito.verify(authPairService, Mockito.times(1)).deleteAllPairsForAccount(account);
        Assertions.assertEquals(expectedAuthResponse, actualAuthResponse);
    }


}
