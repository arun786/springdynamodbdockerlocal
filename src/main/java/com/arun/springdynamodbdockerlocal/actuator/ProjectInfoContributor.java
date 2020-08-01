package com.arun.springdynamodbdockerlocal.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * @author arun on 7/27/20
 */
@Component
public class ProjectInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("project_name", "Spring Service with Dynamo DB and H2 Database")
                .withDetail("Owned by", "Arun")
                .withDetail("point_of_contact", "Arun");
    }
}
