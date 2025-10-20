package com.alxsshv.nativecodedistributionservice.service;

import com.alxsshv.nativecodedistributionservice.AbstractIntegrationTest;
import com.alxsshv.nativecodedistributionservice.dto.CodeKafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
class KafkaTopicReadServiceIntegrTest extends AbstractIntegrationTest {



    @Test
    @DisplayName("Test CodeNotificationListener")
    void testKafkaListener() throws JsonProcessingException {
        String email = "test@email.com";
        String code = "123456";
        CodeKafkaMessage codeKafkaMessage = new CodeKafkaMessage(email, code);
        String message = objectMapper.writeValueAsString(codeKafkaMessage);
        String key = UUID.randomUUID().toString();

        kafkaProducer.send(new ProducerRecord<>(topicName, key, message));
        kafkaProducer.close();

        verify(codeSendingService, timeout(1000).times(1)).send(email, code);
        KafkaTopicReadService.stopReading();
    }

}
