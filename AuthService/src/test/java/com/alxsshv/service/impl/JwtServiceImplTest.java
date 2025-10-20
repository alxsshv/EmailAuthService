package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JWTServiceImpl jwtService;


    @Test
    @DisplayName("Test generateAccessToken when account is not null then return valid accessToken")
    void testGenerateAccessToken_whenAccountIsNotNull_thenReturnValidToken() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        Jwt jwt = Jwt
                .withTokenValue("StringToken")
                .header("authotities", Set.of(Authorities.READ_ONLY))
                .subject(testAccount.getEmail())
                .build();
        ReflectionTestUtils.setField(jwtService, "applicationName", "application");
        ReflectionTestUtils.setField(jwtService, "accessTokenTtl", Duration.of(10, ChronoUnit.MINUTES));
        ReflectionTestUtils.setField(jwtService, "issuer", "http://localhost:8080/");
        Mockito.when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = jwtService.generateAccessToken(testAccount);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        Mockito.verify(jwtEncoder, Mockito.times(1)).encode(any(JwtEncoderParameters.class));

    }

    @Test
    @DisplayName("Test generateAccessToken when account is null then throw IllegalArgumentException")
    void testGenerateAccessToken_whenAccountIsNull_thenThrowException() {

        ReflectionTestUtils.setField(jwtService, "applicationName", "application");
        ReflectionTestUtils.setField(jwtService, "accessTokenTtl", Duration.of(10, ChronoUnit.MINUTES));
        ReflectionTestUtils.setField(jwtService, "issuer", "http://localhost:8080/");

        Assertions.assertThrows(IllegalArgumentException.class, () -> jwtService.generateAccessToken(null));

    }
}
