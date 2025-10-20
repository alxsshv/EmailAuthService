package com.alxsshv.nativecodedistributionservice.service;

import com.alxsshv.nativecodedistributionservice.dto.CodeKafkaMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Properties;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class KafkaTopicReadService {
    private static volatile boolean isStopped = false;

    private final Properties kafkaConsumerProperties;

    private final CodeSendingService codeSendingService;

    public static void stopReading() {
        KafkaTopicReadService.isStopped = true;
    }

    @Async
    public void readFromTopic(Set<String> topics) {
        try (KafkaConsumer<String, CodeKafkaMessage> consumer  = new KafkaConsumer<>(kafkaConsumerProperties)) {
            consumer.subscribe(topics);
            log.info("Started reading kafka data");

            while (!isStopped) {
                try {
                    ConsumerRecords<String, CodeKafkaMessage> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, CodeKafkaMessage> rec : records) {
                        CodeKafkaMessage codeKafkaMessage = rec.value();
                        codeSendingService.send(codeKafkaMessage.email(), codeKafkaMessage.code());
                    }
                } catch (Exception e) {
                    log.error("Error during poll: {}", e.getMessage());
                }
                log.info("Reading kafka topics has been stopped");
            }

        }

    }


}
