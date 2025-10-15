package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.Authorities;
import com.alxsshv.exception.JwtProcessingException;
import com.alxsshv.service.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JwtService {
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${app.secret.access-token.ttl}")
    private Duration accessTokenTtl;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private final JwtEncoder jwtEncoder;

    @Override
    public String generateAccessToken(Account account) {
        String authorities = account.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(accessTokenTtl))
                .subject(account.getEmail())
                .claim(AUTHORITIES_CLAIM_NAME, authorities)
                .build();
        JwtEncoderParameters jwtParameters = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(jwtParameters).getTokenValue();
    }


    @Override
    public String getTokenSubject(String token) {
        JWTClaimsSet claims = parseToken(token);
        validateDecodedToken(claims);
        return claims.getSubject();
    }

    private JWTClaimsSet parseToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (ParseException ex) {
            throw new JwtProcessingException("Token is invalid");
        }
    }

    private void validateDecodedToken(JWTClaimsSet decodedToken) {
        String email = decodedToken.getSubject();
        if (email == null || email.isEmpty()) {
            throw new JwtProcessingException("Token does not contain a subject");
        }
        Instant expireDate = decodedToken.getExpirationTime().toInstant();
        if (expireDate.isBefore(Instant.now())) {
            throw new JwtProcessingException("Token is expired");
        }
    }
}
