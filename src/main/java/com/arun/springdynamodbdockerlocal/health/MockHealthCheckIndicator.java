package com.arun.springdynamodbdockerlocal.health;

import com.arun.springdynamodbdockerlocal.config.UrlConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * @author arun on 7/25/20
 */

@Component
@Slf4j
public class MockHealthCheckIndicator implements HealthIndicator {

    private final WebClient webClient;
    private final UrlConfig urlConfig;

    public MockHealthCheckIndicator(WebClient webClient, UrlConfig urlConfig) {
        this.webClient = webClient;
        this.urlConfig = urlConfig;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return health();
    }

    @Override
    public Health health() {
        Health mockHealth = Health.down().build();
        try {
            Mono<JsonNode> jsonNodeMono = webClient.method(HttpMethod.GET).uri(URI.create(urlConfig.getMockActuatorUrl()))
                    .retrieve().bodyToMono(JsonNode.class);
            JsonNode jsonNode = jsonNodeMono.blockOptional().orElseThrow();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> valueStatus = objectMapper.readValue(String.valueOf(jsonNode), new TypeReference<>() {
            });

            if (valueStatus.get("status").equals("UP")) {
                mockHealth = Health.up().build();
                log.info("Mock service is {}", mockHealth);
            }
        } catch (Exception e) {
            log.error("Mock service is {}", mockHealth);
        }

        return mockHealth;
    }
}
