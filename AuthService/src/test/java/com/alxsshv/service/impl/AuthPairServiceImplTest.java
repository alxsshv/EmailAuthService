package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.repository.AuthPairRepository;
import com.alxsshv.utils.SecurityCodeGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class AuthPairServiceImplTest {

    @Mock
    private SecurityCodeGenerator codeGenerator;

    @Mock
    private AuthPairRepository authPairRepository;

    @InjectMocks
    private AuthPairServiceImpl authPairService;

    @Test
    @DisplayName("Test createAndSaveAuthPair when invoke method then return valid authPair")
    void testCreateAndSaveAuthPair_whenInvokeMethod_thenReturnValidAuthPair() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        String expectedCode = "123456";
        Mockito.when(codeGenerator.generateCodeAsString()).thenReturn(expectedCode);
        Mockito.when(authPairRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        AuthPair authPair = authPairService.createAndSaveAuthPair(testAccount);

        Mockito.verify(codeGenerator, Mockito.times(1)).generateCodeAsString();
        Mockito.verify(authPairRepository, Mockito.times(1)).save(any());
        Assertions.assertEquals(expectedCode, authPair.getCode());
        Assertions.assertEquals(testAccount.getEmail(), authPair.getEmail());
        Assertions.assertNotNull(authPair.getId());
    }

    @Test
    @DisplayName("Test getByEmail method when authPair is found then return authPair")
    void testGetByEmail_whenAuthPairIsFound_thenReturnThisPair() {
        String email = "test@email.com";
        AuthPair authPair = AuthPair.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .code("123456")
                .expirationInSeconds(1L)
                .build();
        Mockito.when(authPairRepository.findByEmail(email)).thenReturn(Optional.of(authPair));

        AuthPair actualAuthPair = authPairService.getByEmail(email);

        Assertions.assertNotNull(actualAuthPair);
        Assertions.assertEquals(authPair, actualAuthPair);
        Mockito.verify(authPairRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test getByEmail method when authPair is not found then throw EntityNotFoundException")
    void testGetByEmail_whenAuthPairIsNotFound_thenThrowException() {
        String email = "test@email.com";
        Mockito.when(authPairRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> authPairService.getByEmail(email));

        Mockito.verify(authPairRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Test deleteAllPairsForAccount when invoke method then invoke authPairRepository's method deleteAllByEmail")
    void testDeleteAllPairsForAccount_whenInvokeMethod_thenInvokeDeleteAllByEmailInRepository() {
        String email = "test@email.com";
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email(email)
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        authPairService.deleteAllPairsForAccount(testAccount);

        Mockito.verify(authPairRepository, Mockito.times(1)).deleteAllByEmail(email);

    }
}
