package com.alxsshv.exception;

public class JwtProcessingException extends RuntimeException {
    public JwtProcessingException(String message) {
        super(message);
    }
}
