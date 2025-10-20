package com.alxsshv.nativecodedistributionservice.serializer;



import com.alxsshv.nativecodedistributionservice.dto.CodeKafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CodeKafkaMessageJsonDeserializerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private CodeKafkaMessageJsonDeserializer deserializer = new CodeKafkaMessageJsonDeserializer();


    @Test
    @DisplayName("Test deserialize method when method arguments contains valid data then return CodeKafkaMessage")
    void testDeserialize_whenDeserializeDataIsValid_thenReturnCodeKafkaMessage() throws JsonProcessingException {
        String email = "test@email.com";
        String code = "123456";
        String topicName = "test-topic";
        CodeKafkaMessage expectedCodeKafkaMessage = new CodeKafkaMessage(email, code);
        byte[] data = objectMapper.writeValueAsBytes(expectedCodeKafkaMessage);

        CodeKafkaMessage actualCodeKafkaMessage = deserializer.deserialize(topicName, data);

        Assertions.assertNotNull(actualCodeKafkaMessage);
        Assertions.assertEquals(expectedCodeKafkaMessage, actualCodeKafkaMessage);
    }


    @Test
    @DisplayName("Test deserialize method when method arguments contains not valid data then return null ")
    void testDeserialize_whenSetNotCorrectByteArray_thenReturnNull() throws JsonProcessingException {
        String topicName = "test-topic";
        byte[] data = {1, 2, 3, 4, 5};

        CodeKafkaMessage actualCodeKafkaMessage = deserializer.deserialize(topicName, data);

        Assertions.assertNull(actualCodeKafkaMessage);
    }

    @Test
    @DisplayName("Test deserialize method when byte array is null then return null ")
    void testDeserialize_whenBytesIsNull_thenReturnNull() throws JsonProcessingException {
        String topicName = "test-topic";
        byte[] data = null;

        CodeKafkaMessage actualCodeKafkaMessage = deserializer.deserialize(topicName, data);

        Assertions.assertNull(actualCodeKafkaMessage);
    }




}
