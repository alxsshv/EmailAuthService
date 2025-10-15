package com.alxsshv.exception;

public class AuthMethodNotSupportedException extends RuntimeException {
    public AuthMethodNotSupportedException(String message) {
        super(message);
    }
}
