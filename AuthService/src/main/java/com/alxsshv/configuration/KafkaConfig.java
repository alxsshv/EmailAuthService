package com.alxsshv.configuration;

import com.alxsshv.dto.CodeKafkaMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.codeNotificationGroupId}")
    private String codeNotificationGroupId;


    @Bean
    public ProducerFactory<String, CodeKafkaMessage> codeNotificationProducersFactory(ObjectMapper objectMapper) {
        Map<String, Object> atMostOnceConfig = new HashMap<>();
        atMostOnceConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        atMostOnceConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        atMostOnceConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        atMostOnceConfig.put(ProducerConfig.ACKS_CONFIG, "0");
        atMostOnceConfig.put(ProducerConfig.RETRIES_CONFIG, 0);

        return new DefaultKafkaProducerFactory<>(atMostOnceConfig, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, CodeKafkaMessage> kafkaTemplate(ProducerFactory<String, CodeKafkaMessage> codeNotificationProducersFactory) {
        return new KafkaTemplate<>(codeNotificationProducersFactory);
    }




}
