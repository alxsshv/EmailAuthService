package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.repository.AuthPairRepository;
import com.alxsshv.service.AuthPairService;
import com.alxsshv.utils.SecurityCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
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
        AuthPair pair = authPairRepository.save(authPair);
        System.out.println("Код сохранён в Redis " + pair.getCode());
        return authPair;
    }

    @Override
    public Set<AuthPair> getAllByEmail(String email) {
        return authPairRepository.findAllByEmail(email);
    }

    @Override
    public void deleteAllPairsForAccount(Account account) {
        authPairRepository.deleteAllByEmail(account.getEmail());
    }

}
