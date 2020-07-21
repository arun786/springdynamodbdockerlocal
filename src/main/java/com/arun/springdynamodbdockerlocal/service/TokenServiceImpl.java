package com.arun.springdynamodbdockerlocal.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.arun.springdynamodbdockerlocal.config.TokenLimitConfig;
import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
import com.arun.springdynamodbdockerlocal.repository.TokenDynamoDB;
import com.arun.springdynamodbdockerlocal.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author arun on 7/18/20
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenDynamoDB tokenDynamoDB;
    private final TokenLimitConfig tokenLimitConfig;

    public TokenServiceImpl(TokenRepository tokenRepository, TokenDynamoDB tokenDynamoDB, TokenLimitConfig tokenLimitConfig) {
        this.tokenRepository = tokenRepository;
        this.tokenDynamoDB = tokenDynamoDB;
        this.tokenLimitConfig = tokenLimitConfig;
    }

    @Override
    public List<Token> getToken(String actorId, List<TokenRequest> tokenRequests) {

        List<Map<String, AttributeValue>> tokenItems = tokenDynamoDB.getTokenItems(actorId);
        int requestSize = tokenRequests.size();

        Map<String, List<String>> actorDetails = getActorDetails(tokenItems, requestSize);
        int totalCountFor24Hrs = 0;
        int totalCountFor30Days = 0;
        int ttlFor24Hrs = 0;
        int ttlFor30Days = 0;
        if (!actorDetails.isEmpty()) {
            List<String> lst24Hrs = actorDetails.get("24Hr");
            List<String> lst30Days = actorDetails.get("30Days");


            if (lst24Hrs != null) {
                totalCountFor24Hrs = Integer.parseInt(lst24Hrs.get(0));
                ttlFor24Hrs = Integer.parseInt(lst24Hrs.get(1));
            } else {
                totalCountFor24Hrs = requestSize;
            }
            if (lst30Days != null) {
                totalCountFor30Days = Integer.parseInt(lst30Days.get(0));
                ttlFor30Days = Integer.parseInt(lst30Days.get(1));
            } else {
                totalCountFor30Days = requestSize;
            }
        }


        if (tokenItems.isEmpty()) {
            List<Token> tokensByUuid = tokenRepository.getTokensByUuid(actorId);
            //TODO logic for tokenRequests
            tokenDynamoDB.updateTokenItems(actorId, tokenRequests, requestSize, requestSize, ttlFor24Hrs, ttlFor30Days);
            return tokensByUuid;
        } else if (validateActorEligible(totalCountFor24Hrs, totalCountFor30Days)) {
            List<Token> tokensByUuid = tokenRepository.getTokensByUuid(actorId);
            //todo logic for filtering token
            tokenDynamoDB.updateTokenItems(actorId, tokenRequests, requestSize, requestSize, ttlFor24Hrs, ttlFor30Days);
            return tokensByUuid;
        } else {
            throw new RuntimeException("Limit Exceeded");
        }
    }

    /**
     * @param totalCountFor24Hrs  - the number of requests which is allowed in 24 hrs. (though the period can be changed from yml file)
     * @param totalCountFor30Days - the number of requests which is allowed for 30 days. (though the period can be changed from yml file)
     * @return - true if the actor is eligible else false
     */
    private boolean validateActorEligible(int totalCountFor24Hrs, int totalCountFor30Days) {
        int tokenLimitFor24hr = Integer.parseInt(tokenLimitConfig.getTokenLimitFor24hr());
        int tokenLimitFor30day = Integer.parseInt(tokenLimitConfig.getTokenLimitFor30day());

        if (totalCountFor30Days > tokenLimitFor30day) {
            return false;
        }

        return totalCountFor24Hrs < tokenLimitFor24hr;
    }

    private Map<String, List<String>> getActorDetails(List<Map<String, AttributeValue>> tokenItems, int requestTokenCount) {

        Map<String, List<String>> actorDetails = new HashMap<>();

        for (Map<String, AttributeValue> tokens : tokenItems) {
            String duration = tokens.get(tokenLimitConfig.getDuration()).getS();
            int count = Integer.parseInt(tokens.get(tokenLimitConfig.getCount()).getN());
            String ttl = tokens.get(tokenLimitConfig.getTtl()).getN();

            if (duration.equals(tokenLimitConfig.getFor24Hr())) {
                List<String> for24Hr = new ArrayList<>();
                int totalCount = count + requestTokenCount;
                for24Hr.add(String.valueOf(totalCount));
                for24Hr.add(ttl);
                actorDetails.put("24Hr", for24Hr);
            } else if (duration.equals(tokenLimitConfig.getFor30Day())) {
                List<String> for30Days = new ArrayList<>();
                int totalCount = count + requestTokenCount;
                for30Days.add(String.valueOf(totalCount));
                for30Days.add(ttl);
                actorDetails.put("30Days", for30Days);
            }
        }

        return actorDetails;
    }
}
