package com.alxsshv;


import com.alxsshv.repository.AccountRepository;
import com.alxsshv.repository.AuthPairRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
public abstract class AbstractIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:4.0.1"));

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Container
    static final RedisContainer redis = new RedisContainer("redis:8.0.3").withExposedPorts(6379);

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected AuthPairRepository authPairRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void registryApplicationProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379).toString());
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


}
