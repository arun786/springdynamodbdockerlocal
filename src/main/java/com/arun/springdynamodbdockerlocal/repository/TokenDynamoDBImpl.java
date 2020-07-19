package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.arun.springdynamodbdockerlocal.config.AwsConfig;
import com.arun.springdynamodbdockerlocal.config.TokenLimitConfig;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
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
    private final TokenLimitConfig tokenLimitConfig;

    @Autowired
    public TokenDynamoDBImpl(AwsConfig awsConfig, AmazonDynamoDB amazonDynamoDB, TokenLimitConfig tokenLimitConfig) {
        this.awsConfig = awsConfig;
        this.amazonDynamoDB = amazonDynamoDB;
        this.tokenLimitConfig = tokenLimitConfig;
    }

    @Override
    public List<Map<String, AttributeValue>> getTokenItems(String actorId) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.withTableName(awsConfig.getTableName())
                .withKeyConditions(ImmutableMap.of(tokenLimitConfig.getActorId(), new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue(actorId))));
        QueryResult query = amazonDynamoDB.query(queryRequest);
        return query.getItems();
    }

    @Override
    public boolean updateTokenItems(String actorId, List<TokenRequest> tokens, List<Integer> counts) {
        Map<String, AttributeValue> item24Hr = new HashMap<>();
        Map<String, AttributeValue> item30Day = new HashMap<>();
        int size = tokens.size();
        int totalCountFor24Hr = size + counts.get(0);
        int totalCountFor30Days = size + counts.get(1);

        item24Hr.put(tokenLimitConfig.getActorId(), new AttributeValue().withS(actorId));
        item24Hr.put(tokenLimitConfig.getDuration(), new AttributeValue().withS(tokenLimitConfig.getFor24Hr()));
        item24Hr.put(tokenLimitConfig.getCount(), new AttributeValue().withN(String.valueOf(totalCountFor24Hr)));

        item30Day.put(tokenLimitConfig.getActorId(), new AttributeValue().withS(actorId));
        item30Day.put(tokenLimitConfig.getDuration(), new AttributeValue().withS(tokenLimitConfig.getFor30Day()));
        item30Day.put(tokenLimitConfig.getCount(), new AttributeValue().withN(String.valueOf(totalCountFor30Days)));

        PutItemRequest putItemFor24Hr = new PutItemRequest(awsConfig.getTableName(), item24Hr);
        PutItemRequest putItemFor30Days = new PutItemRequest(awsConfig.getTableName(), item30Day);
        amazonDynamoDB.putItem(putItemFor24Hr);
        amazonDynamoDB.putItem(putItemFor30Days);
        return true;
    }
}
