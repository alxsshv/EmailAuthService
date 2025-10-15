package com.alxsshv.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecurityCodeGenerator {
    private static final int MIN_VALUE = 10000;
    private static final int MAX_VALUE = 999999;
    private static final int RANGE = (MAX_VALUE-MIN_VALUE);
    private static final SecureRandom RANDOM = new SecureRandom();


    public String generateCodeAsString() {
        int code = RANDOM.nextInt(RANGE) + MIN_VALUE;
        return String.valueOf(code);
    }

}
