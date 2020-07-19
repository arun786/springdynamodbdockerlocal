package com.arun.springdynamodbdockerlocal.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.arun.springdynamodbdockerlocal.config.TokenLimitConfig;
import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
import com.arun.springdynamodbdockerlocal.repository.TokenDynamoDB;
import com.arun.springdynamodbdockerlocal.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author arun on 7/18/20
 */
@Service
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
    public List<Token> getToken(String actorId, List<TokenRequest> tokens) {

        List<Map<String, AttributeValue>> tokenItems = tokenDynamoDB.getTokenItems(actorId);

        int countFor24Hr = 0;
        int countFor30days = 0;

        List<Integer> counts = new ArrayList<>(2);
        counts.add(countFor24Hr);
        counts.add(countFor30days);

        if (tokenItems.isEmpty() || validateActorEligible(tokenItems, counts)) {
            List<Token> tokensByUuid = tokenRepository.getTokensByUuid(actorId);
            //TODO logic for tokens
            tokenDynamoDB.updateTokenItems(actorId, tokens, counts);
            return tokensByUuid;
        } else {
            //TODO throw an exception saying limit reached.
            throw new RuntimeException("Limit Exceeded");
        }
    }

    private boolean validateActorEligible(List<Map<String, AttributeValue>> tokenItems, List<Integer> counts) {
        int tokenLimitFor24hr = Integer.parseInt(tokenLimitConfig.getTokenLimitFor24hr());
        int tokenLimitFor30day = Integer.parseInt(tokenLimitConfig.getTokenLimitFor30day());

        for (Map<String, AttributeValue> tokens : tokenItems) {
            String duration = tokens.get(tokenLimitConfig.getDuration()).getS();
            int count = Integer.parseInt(tokens.get(tokenLimitConfig.getCount()).getN());

            if (duration.equals(tokenLimitConfig.getFor24Hr())) {
                int existingCountFor24Hr = counts.get(0) + count;
                counts.set(0, existingCountFor24Hr);
            } else if (duration.equals(tokenLimitConfig.getFor30Day())) {
                int existingCountFor30Days = counts.get(1) + count;
                counts.set(1, existingCountFor30Days);
            }
        }

        if (counts.get(1) > tokenLimitFor30day) {
            return false;
        } else return counts.get(0) < tokenLimitFor24hr;
    }
}
