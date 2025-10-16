package com.alxsshv.codedistributionservice.service.impl;

import com.alxsshv.codedistributionservice.service.CodeSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class EmailSendingService implements CodeSendingService {
    String emailTemplate = ("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Override
    public void send(Object to, String code) {

        if (to instanceof String email && isEmail(email)) {
            log.info("Authorization code: {} has been sent to email {}", code, email);
        } else {
            log.error("This service not supported sending for this address type");
        }
    }

    private boolean isEmail(String s) {
        return Pattern.matches(emailTemplate, s);
    }
}
