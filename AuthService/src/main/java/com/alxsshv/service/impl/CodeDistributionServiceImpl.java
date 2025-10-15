package com.alxsshv.service.impl;

import com.alxsshv.entity.AuthPair;
import com.alxsshv.service.CodeDistributionService;
import org.springframework.stereotype.Service;

@Service
public class CodeDistributionServiceImpl implements CodeDistributionService {
    @Override
    public void sendCode(AuthPair authPair) {
        System.out.printf("Вам выдан код авторизации: %s %s", authPair.getCode(), System.lineSeparator());
    }
}
