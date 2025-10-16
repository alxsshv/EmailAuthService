package com.alxsshv.codedistributionservice.configuration;

import com.alxsshv.codedistributionservice.dto.CodeKafkaMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.codeNotificationGroupId}")
    private String codeNotificationGroupId;

    @Bean
    public ConsumerFactory<String, CodeKafkaMessage> codeNotificationConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> atMostOnceConfig = new HashMap<>();
        atMostOnceConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        atMostOnceConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        atMostOnceConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        atMostOnceConfig.put(ConsumerConfig.GROUP_ID_CONFIG, codeNotificationGroupId);
        atMostOnceConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        atMostOnceConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        atMostOnceConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(atMostOnceConfig, new StringDeserializer(), new JsonDeserializer<>(CodeKafkaMessage.class, objectMapper, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CodeKafkaMessage> codeNotificationKafkaListenerContainerFactory(
            ConsumerFactory<String, CodeKafkaMessage> codeNotificationConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CodeKafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(codeNotificationConsumerFactory);
        return factory;
    }

}
