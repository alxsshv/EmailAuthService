package com.alxsshv.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("auth_pairs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AuthPair {
        @Id
        @Indexed
        private String id;

        @Indexed
        private String email;

        @Indexed
        private String code;

        @TimeToLive
        private Long expirationInSeconds;
}

