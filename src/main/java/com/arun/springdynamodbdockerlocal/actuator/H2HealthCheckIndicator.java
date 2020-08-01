package com.arun.springdynamodbdockerlocal.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

/**
 * @author arun on 7/27/20
 */
@Component
public class H2HealthCheckIndicator implements HealthIndicator {
    @Override
    public Health health() {

        boolean isValid = Runtime.getRuntime().maxMemory() > (100 * 1024 * 1024);
        Status status = isValid ? Status.UP : Status.DOWN;
        return Health.status(status).build();
    }
}
