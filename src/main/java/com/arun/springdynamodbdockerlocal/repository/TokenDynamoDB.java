package com.arun.springdynamodbdockerlocal.repository;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.arun.springdynamodbdockerlocal.model.request.Tokens;

import java.util.List;
import java.util.Map;

/**
 * @author arun on 7/18/20
 */
public interface TokenDynamoDB {
    List<Map<String, AttributeValue>> getTokenItems(String actorId);

    boolean updateTokenItems(String actorId, List<Tokens> tokens, List<Integer> counts);
}
