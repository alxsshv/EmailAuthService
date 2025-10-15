package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
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

}
