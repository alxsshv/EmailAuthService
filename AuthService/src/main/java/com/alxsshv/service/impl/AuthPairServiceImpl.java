package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.repository.AuthPairRepository;
import com.alxsshv.service.AuthPairService;
import com.alxsshv.utils.SecurityCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthPairServiceImpl implements AuthPairService {

    @Value("${app.secret.auth-code.ttl-in-seconds}")
    private Long authPairTtl;

    private final SecurityCodeGenerator codeGenerator;

    private final AuthPairRepository authPairRepository;

    @Transactional
    public AuthPair createAndSaveAuthPair(Account account) {
        String code = codeGenerator.generateCodeAsString();
        AuthPair authPair = new AuthPair(UUID.randomUUID().toString(), account.getEmail(), code, authPairTtl);
        authPairRepository.deleteAllByEmail(account.getEmail());
        return authPairRepository.save(authPair);
    }

    @Override
    public AuthPair getByEmail(String email) {
        Optional<AuthPair> authPairOpt = authPairRepository.findByEmail(email);
        return authPairOpt.orElseThrow(
                () -> new EntityNotFoundException(String.format("Code for email %s not found, please request new authorization code", email)));
    }

    @Override
    @Transactional
    public void deleteAllPairsForAccount(Account account) {
        authPairRepository.deleteAllByEmail(account.getEmail());
    }

}
