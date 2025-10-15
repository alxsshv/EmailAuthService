package com.alxsshv.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "app.secret.rsa")
public record RsaProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
