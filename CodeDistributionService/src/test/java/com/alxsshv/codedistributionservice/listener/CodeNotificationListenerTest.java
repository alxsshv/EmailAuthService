package com.alxsshv.codedistributionservice.listener;

import com.alxsshv.codedistributionservice.dto.CodeKafkaMessage;
import com.alxsshv.codedistributionservice.service.impl.EmailSendingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CodeNotificationListenerTest {

    EmailSendingService emailSendingService = Mockito.mock(EmailSendingService.class);

    CodeNotificationListener listener = new CodeNotificationListener(emailSendingService);

    @Test
    void testListenCodeNotification_whenInvokeMethode_thenSendMessage() {
        String email = "test@email.com";
        String code = "123456";
        CodeKafkaMessage message = new CodeKafkaMessage(email, code);
        String key = UUID.randomUUID().toString();
        String topicName = "test-topic";
        int partition = 0;
        Long timestamp = Instant.now().getLong(ChronoField.INSTANT_SECONDS);

        listener.listenCodeNotification(message, key, topicName, partition, timestamp);

        verify(emailSendingService, times(1)).send(email, code);

    }
}
