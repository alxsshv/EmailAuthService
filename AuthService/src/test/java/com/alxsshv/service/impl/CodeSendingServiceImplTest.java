package com.alxsshv.service.impl;

import com.alxsshv.dto.CodeKafkaMessage;
import com.alxsshv.entity.AuthPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class CodeSendingServiceImplTest {

    @Mock
    private KafkaTemplate<String, CodeKafkaMessage> kafkaTemplate;

    @InjectMocks
    private CodeSendingServiceImpl codeSendingService;

    @Test
    @DisplayName("Test sendCode method when authPair is not null then message has been sent")
    void testSendCode_whenAuthPairNotNull_thenMessageHasBeenSent() {
        AuthPair authPair = AuthPair.builder()
                .id(UUID.randomUUID().toString())
                .email("test@email.com")
                .code("123456")
                .expirationInSeconds(1L)
                .build();

        ReflectionTestUtils.setField(codeSendingService, "codeNotificationTopic", "test-topic");
        codeSendingService.sendCode(authPair);

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(anyString(), any(), any(CodeKafkaMessage.class));
    }

    @Test
    @DisplayName("Test sendCode method when authPair is null then message has not been sent")
    void testSendCode_whenAuthPairIsNull_thenMessageHasNotBeenSent() {
        codeSendingService.sendCode(null);

        Mockito.verify(kafkaTemplate, Mockito.never()).send(anyString(), any(), any());
    }




}
