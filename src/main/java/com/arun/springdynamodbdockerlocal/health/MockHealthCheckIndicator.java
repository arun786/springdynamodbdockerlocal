package com.arun.springdynamodbdockerlocal.health;

import com.arun.springdynamodbdockerlocal.config.UrlConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * @author arun on 7/25/20
 */

//@Component
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
        Mono<JsonNode> jsonNodeMono = webClient.method(HttpMethod.GET).uri(URI.create(urlConfig.getMockActuatorUrl()))
                .retrieve().bodyToMono(JsonNode.class);


        JsonNode jsonNode = jsonNodeMono.blockOptional().orElseThrow();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> valueStatus = objectMapper.readValue(String.valueOf(jsonNode), new TypeReference<>() {
            });

            return valueStatus.get("status").equals("UP") ? Health.up().build() : Health.down().build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return Health.down().build();
    }
}
