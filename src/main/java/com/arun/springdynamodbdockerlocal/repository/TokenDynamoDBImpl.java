package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.arun.springdynamodbdockerlocal.config.AwsConfig;
import com.arun.springdynamodbdockerlocal.model.request.Tokens;
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
public class TokenDynamoDBImpl implements TokenDynamoDB {

    private final AwsConfig awsConfig;
    private final AmazonDynamoDB amazonDynamoDB;

    @Autowired
    public TokenDynamoDBImpl(AwsConfig awsConfig, AmazonDynamoDB amazonDynamoDB) {
        this.awsConfig = awsConfig;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @Override
    public List<Map<String, AttributeValue>> getTokenItems(String actorId) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.withTableName(awsConfig.getTableName())
                .withKeyConditions(ImmutableMap.of("actorId", new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(actorId))));
        QueryResult query = amazonDynamoDB.query(queryRequest);
        return query.getItems();
    }

    @Override
    public boolean updateTokenItems(String actorId, List<Tokens> tokens, List<Integer> counts) {
        Map<String, AttributeValue> item24Hr = new HashMap<>();
        Map<String, AttributeValue> item30Day = new HashMap<>();
        int size = tokens.size() / 2;
        int totalCountFor24Hr = size + counts.get(0);
        int totalCountFor30Days = size + counts.get(1);

        item24Hr.put("actorId", new AttributeValue().withS(actorId));
        item24Hr.put("duration", new AttributeValue().withS("24 hrs"));
        item24Hr.put("count", new AttributeValue().withN(String.valueOf(totalCountFor24Hr)));

        item30Day.put("actorId", new AttributeValue().withS(actorId));
        item30Day.put("duration", new AttributeValue().withS("30 days"));
        item30Day.put("count", new AttributeValue().withN(String.valueOf(totalCountFor30Days)));

        PutItemRequest putItemFor24Hr = new PutItemRequest(awsConfig.getTableName(), item24Hr);
        PutItemRequest putItemFor30Days = new PutItemRequest(awsConfig.getTableName(), item30Day);
        amazonDynamoDB.putItem(putItemFor24Hr);
        amazonDynamoDB.putItem(putItemFor30Days);
        return true;
    }
}
