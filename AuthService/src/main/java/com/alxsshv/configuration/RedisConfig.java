package com.alxsshv.configuration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        return new JedisConnectionFactory(configuration);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        final RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
//        return template;
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//        return container;
//    }

}
