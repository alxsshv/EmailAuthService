package com.alxsshv.codedistributionservice.listener;


import com.alxsshv.codedistributionservice.dto.CodeKafkaMessage;
import com.alxsshv.codedistributionservice.service.CodeSendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CodeNotificationListener {

    private final CodeSendingService codeSendingService;

    @KafkaListener(
            topics = "${app.kafka.codeNotificationTopic}",
            groupId = "${app.kafka.codeNotificationGroupId}",
            containerFactory = "codeNotificationKafkaListenerContainerFactory")
    public void listenCodeNotification(@Payload CodeKafkaMessage message,
                                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                       @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(value = KafkaHeaders.RECEIVED_PARTITION) int partition,
                                       @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        log.info("Received message: {} with key {} from topic {} and partition {} timeAt {}",
                message.toString(), key, topic, partition, timestamp);
        codeSendingService.send(message.getEmail(), message.getCode());
    }

}
