package com.arun.springdynamodbdockerlocal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 7/25/20
 */
@Configuration
@ConfigurationProperties(prefix = "client.url")
@EnableConfigurationProperties
@Getter
@Setter
public class UrlConfig {
    private String mockUrl;
    private String mockActuatorUrl;
}
