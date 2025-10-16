package com.alxsshv.service.impl;

import com.alxsshv.dto.CodeKafkaMessage;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.service.CodeSendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeSendingServiceImpl implements CodeSendingService {

    @Value("${app.kafka.codeNotificationTopic}")
    private String codeNotificationTopic;

    private final KafkaTemplate<String, CodeKafkaMessage> kafkaTemplate;

    @Override
    public void sendCode(AuthPair authPair) {
        log.info("Вам выдан код авторизации: {}", authPair.getCode());
        CodeKafkaMessage message = new CodeKafkaMessage(authPair.getEmail(), authPair.getCode());
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(codeNotificationTopic, key, message);
        log.info("Authorization code send to CodeDistributionService for email {} with key {} ", authPair.getEmail(), key);
    }
}
