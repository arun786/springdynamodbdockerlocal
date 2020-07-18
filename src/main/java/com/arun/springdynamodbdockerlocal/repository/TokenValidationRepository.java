package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.arun.springdynamodbdockerlocal.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author arun on 7/18/20
 */
@Repository
public class TokenValidationRepository {

    private final AwsConfig awsConfig;
    private final AmazonDynamoDB amazonDynamoDB;

    @Autowired
    public TokenValidationRepository(AwsConfig awsConfig, AmazonDynamoDB amazonDynamoDB) {
        this.awsConfig = awsConfig;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public void getToken(String actorId){

        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("customerId", new AttributeValue().withS(actorId));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(awsConfig.getTableName())
                .withKey(itemKey)
                .withConsistentRead(true);

        GetItemResult item = amazonDynamoDB.getItem(getItemRequest);
    }


}
