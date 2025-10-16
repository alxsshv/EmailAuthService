package com.alxsshv.nativecodedistributionservice.exception;

public class KafkaValueDeserializationException extends RuntimeException {
  public KafkaValueDeserializationException(String message) {
    super(message);
  }
}
