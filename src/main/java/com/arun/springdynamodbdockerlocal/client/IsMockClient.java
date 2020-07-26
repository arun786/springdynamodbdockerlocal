package com.arun.springdynamodbdockerlocal.client;

import com.arun.springdynamodbdockerlocal.config.UrlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author arun on 7/25/20
 */
@Component
public class IsMockClient {

    private final UrlConfig urlConfig;
    private final WebClient webClient;

    @Autowired
    public IsMockClient(UrlConfig urlConfig, WebClient webClient) {
        this.urlConfig = urlConfig;
        this.webClient = webClient;
    }

    public WebClient getWebClientForMock() {

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlConfig.getMockUrl())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception(clientResponse.statusCode().getReasonPhrase())));
        return null;
    }

}
