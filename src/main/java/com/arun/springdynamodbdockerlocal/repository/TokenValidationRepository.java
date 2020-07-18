package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.arun.springdynamodbdockerlocal.config.AwsConfig;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
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

    public void getToken(String actorId) {

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.withTableName(awsConfig.getTableName())
                .withKeyConditions(ImmutableMap.of("actorId", new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(actorId))));


        QueryResult query = amazonDynamoDB.query(queryRequest);

        List<Map<String, AttributeValue>> items = query.getItems();
        if (items.isEmpty()) {
            putToken(actorId);
        }

        items.forEach(item -> {
            AttributeValue actor = item.get("actorId");
            System.out.println(actor.getS());
            AttributeValue duration = item.get("duration");
            System.out.println(duration.getS());
            AttributeValue count = item.get("count");
            System.out.println(count.getN());
        });

    }

    public void putToken(String actorId) {
        Map<String, AttributeValue> item24Hr = new HashMap<>();
        Map<String, AttributeValue> item30Day = new HashMap<>();

        item24Hr.put("actorId", new AttributeValue().withS(actorId));
        item24Hr.put("duration", new AttributeValue().withS("24 hrs"));
        item24Hr.put("count", new AttributeValue().withN("1"));

        item30Day.put("actorId", new AttributeValue().withS(actorId));
        item30Day.put("duration", new AttributeValue().withS("30 days"));
        item30Day.put("count", new AttributeValue().withN("1"));

        PutItemRequest putItemFor24Hr = new PutItemRequest(awsConfig.getTableName(), item24Hr);
        PutItemRequest putItemFor30Days = new PutItemRequest(awsConfig.getTableName(), item30Day);
        amazonDynamoDB.putItem(putItemFor24Hr);
        amazonDynamoDB.putItem(putItemFor30Days);
    }


}
