package com.alxsshv.nativecodedistributionservice.serializer;

import com.alxsshv.nativecodedistributionservice.dto.CodeKafkaMessage;
import com.alxsshv.nativecodedistributionservice.exception.KafkaValueDeserializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CodeKafkaMessageJsonDeserializer implements Deserializer<CodeKafkaMessage> {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CodeKafkaMessage deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, CodeKafkaMessage.class);
        } catch (Exception ex) {
            String errorMessage = "CodeKafkaMessage deserialization error: " + ex.getMessage();
            log.error(errorMessage);
            throw new KafkaValueDeserializationException(errorMessage);
        }
    }

}
