# springdynamodbdockerlocal

## Create a Docker image of AWS Dynamo DB table

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
