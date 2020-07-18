package com.arun.springdynamodbdockerlocal.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author arun on 7/18/20
 */
@Configuration
public class DynamoDbConfig {

    private AwsConfig awsConfig;

    public DynamoDbConfig(AwsConfig awsConfig) {
        this.awsConfig = awsConfig;
    }

    @Bean
    public AmazonDynamoDB getDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard().
                withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsConfig.getEndPoint(), awsConfig.getRegion())).build();
    }
}
