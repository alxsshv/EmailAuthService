package com.alxsshv.nativecodedistributionservice.service;

import com.alxsshv.nativecodedistributionservice.dto.CodeKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaTopicReadService {

    private final Properties kafkaConsumerProperties;

    private final CodeSendingService codeSendingService;

    public void readFromTopic(Set<String> topics) {
        try (KafkaConsumer<String, CodeKafkaMessage> consumer  = new KafkaConsumer<>(kafkaConsumerProperties)) {
            consumer.subscribe(topics);
            log.info("Started reading kafka data");

            while (true) {
                try {
                    ConsumerRecords<String, CodeKafkaMessage> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, CodeKafkaMessage> record : records) {
                        CodeKafkaMessage codeKafkaMessage = record.value();
                        codeSendingService.send(codeKafkaMessage.email(), codeKafkaMessage.code());
                    }
                } catch (Exception e) {
                    log.error("Error during poll: {}", e.getMessage());
                }
            }

        }

    }


}
