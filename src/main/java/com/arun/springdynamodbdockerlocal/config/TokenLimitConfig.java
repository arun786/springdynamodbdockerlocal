package com.arun.springdynamodbdockerlocal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 7/18/20
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.limit")
@Getter
@Setter
public class TokenLimitConfig {
    private String tokenLimitFor24hr;
    private String tokenLimitFor30day;
}
