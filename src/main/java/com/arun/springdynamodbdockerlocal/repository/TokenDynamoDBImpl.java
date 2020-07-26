package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.arun.springdynamodbdockerlocal.config.AwsConfig;
import com.arun.springdynamodbdockerlocal.constants.Constants;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author arun on 7/18/20
 */
@Repository
@Slf4j
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
                .withKeyConditions(ImmutableMap.of(Constants.actorId, new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(actorId))));
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        try {
            QueryResult query = amazonDynamoDB.query(queryRequest);
            items = query.getItems();
        } catch (AmazonDynamoDBException e) {
            log.error("Error to get the details of actorId {} with error {}", actorId, e.getLocalizedMessage());
        }
        return items;
    }

    @Override
    public boolean updateTokenItems(String actorId, List<TokenRequest> tokens, int totalCountFor24Hrs, int totalCountFor30Days, int ttlFor24Hrs, int ttlFor30Days) {
        Map<String, AttributeValue> item24Hr = new HashMap<>();
        Map<String, AttributeValue> item30Day = new HashMap<>();
        int size = tokens.size();

        int existingCountFor24Hr = totalCountFor24Hrs - size;
        int existingCountFor30Days = totalCountFor30Days - size;

        item24Hr.put(Constants.actorId, new AttributeValue().withS(actorId));
        item24Hr.put(Constants.duration, new AttributeValue().withS(Constants.for24Hr));
        item24Hr.put(Constants.count, new AttributeValue().withN(String.valueOf(totalCountFor24Hrs)));


        item30Day.put(Constants.actorId, new AttributeValue().withS(actorId));
        item30Day.put(Constants.duration, new AttributeValue().withS(Constants.for30Day));
        item30Day.put(Constants.count, new AttributeValue().withN(String.valueOf(totalCountFor30Days)));
        long a = System.currentTimeMillis() / 1000L;


        if (existingCountFor24Hr == 0) {
            //this expires after 60 seconds
            int exp24Hr = Math.toIntExact(a + 60);
            item24Hr.put("ttl", new AttributeValue().withN(String.valueOf(exp24Hr)));
        } else {
            item24Hr.put("ttl", new AttributeValue().withN(String.valueOf(ttlFor24Hrs)));
        }
        if (existingCountFor30Days == 0) {
            //this expires after 1 hour
            int exp30Day = (int) (a + 3600);
            item30Day.put("ttl", new AttributeValue().withN(String.valueOf(exp30Day)));
        } else {
            item30Day.put("ttl", new AttributeValue().withN(String.valueOf(ttlFor30Days)));
        }

        PutItemRequest putItemFor24Hr = new PutItemRequest(awsConfig.getTableName(), item24Hr);
        PutItemRequest putItemFor30Days = new PutItemRequest(awsConfig.getTableName(), item30Day);
        boolean isDynamoUpdated = true;
        try {
            amazonDynamoDB.putItem(putItemFor24Hr);
            amazonDynamoDB.putItem(putItemFor30Days);
        } catch (AmazonDynamoDBException e) {
            log.error("Error to get the details of actorId {} with error {}", actorId, e.getLocalizedMessage());
            isDynamoUpdated = false;
        }

        return isDynamoUpdated;
    }
}
