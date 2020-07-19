# springdynamodbdockerlocal

Requirement of the below repo is as under.

    1. To get the details of token from h2 database.
    2. To limit the count of tokens within 24hrs with a specified number.
    3. To limit the count of tokens within 30 days with a specified number.
    4. the above validation will be done in dynamo db table.
    5. if the limit requested for tokens exceeds the above validation, throw an exception.
    
Below are the steps

## Step 1 : Create a Docker image of AWS Dynamo DB table

Steps to create a local dynamo db table on docker.

switch to docker folder under com/arun/springdynamodbdockerlocal 

and execute the bash script, once the bash script runs using the below

./dynamo.sh

it creates a local docker dynamo db with below details

    1. table as TokenCounter
    2. Primary key as actorId
    3. sort key as duration.

Below is the content of the Dockerfile, which will create a local dynamo DB 

    version: '3'
    
    services:
        dynamodb:
            image: amazon/dynamodb-local
            hostname: dynamodb-local
            container_name: dynamodb_local
            ports:
                - "8042:8000"
                
The below bash script will create an image, run dynamo db in local and
will create table named TokenCounter with primary key as actorId and sort key as duration.


    #!/bin/bash
    
    docker-compose -f Dockerfile up -d
    
    aws dynamodb --endpoint-url http://localhost:8042 create-table \
      --table-name TokenCounter \
      --attribute-definitions AttributeName=actorId,AttributeType=S AttributeName=duration,AttributeType=S \
      --key-schema AttributeName=actorId,KeyType=HASH AttributeName=duration,KeyType=RANGE \
      --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
    
    aws dynamodb --endpoint-url http://localhost:8042 update-time-to-live --table-name TokenCounter --time-to-live-specification "Enabled=true, AttributeName=ttl"


## step 2 : Create a Spring boot application

configure the yaml file with the below for Dynamo DB

     dynamo:
        tableName: "TokenCounter"
        region: "us-west-1"
        endPoint: "http://localhost:8042"
      token:
        uuid: 10
        tokens: 100
    
      limit:
        tokenLimitFor24hr: 10
        tokenLimitFor30day: 20
        duration: "duration"
        For24Hr: "for24Hr"
        For30Day: "for30Day"
        actorId: "actorId"
        count: "count"
        ttlFor24Hr: "60"
        ttlFor30days: "3600"
        ttl: "ttl"
        
        
## Step 3 : Use Dynamo DB specific 


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
        public boolean updateTokenItems(String actorId, List<TokenRequest> tokens, int totalCountFor24Hrs, int totalCountFor30Days, int ttlFor24Hrs, int ttlFor30Days) {
            Map<String, AttributeValue> item24Hr = new HashMap<>();
            Map<String, AttributeValue> item30Day = new HashMap<>();
            int size = tokens.size();
    
            int existingCountFor24Hr = totalCountFor24Hrs - size;
            int existingCountFor30Days = totalCountFor30Days - size;
    
            item24Hr.put(tokenLimitConfig.getActorId(), new AttributeValue().withS(actorId));
            item24Hr.put(tokenLimitConfig.getDuration(), new AttributeValue().withS(tokenLimitConfig.getFor24Hr()));
            item24Hr.put(tokenLimitConfig.getCount(), new AttributeValue().withN(String.valueOf(totalCountFor24Hrs)));
    
    
            item30Day.put(tokenLimitConfig.getActorId(), new AttributeValue().withS(actorId));
            item30Day.put(tokenLimitConfig.getDuration(), new AttributeValue().withS(tokenLimitConfig.getFor30Day()));
            item30Day.put(tokenLimitConfig.getCount(), new AttributeValue().withN(String.valueOf(totalCountFor30Days)));
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
            amazonDynamoDB.putItem(putItemFor24Hr);
            amazonDynamoDB.putItem(putItemFor30Days);
            return true;
        }
    }
