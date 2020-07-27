package com.arun.springdynamodbdockerlocal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author arun on 7/25/20
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.security.user")
@Getter
@Setter
public class SpringSecurityConfig {
    private String name;
    private String password;
    private List<String> roles;
}
