package com.alxsshv.nativecodedistributionservice.configuration;

import com.alxsshv.nativecodedistributionservice.service.KafkaTopicReadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class StartedConfig {


    @Value("${app.kafka.codeNotificationTopic}")
    private String codeNotificationTopic;


    private final KafkaTopicReadService kafkaTopicReadService;

    @PostConstruct
    public void startKafkaReading() {
        kafkaTopicReadService.readFromTopic(Set.of(codeNotificationTopic));
    }

}
