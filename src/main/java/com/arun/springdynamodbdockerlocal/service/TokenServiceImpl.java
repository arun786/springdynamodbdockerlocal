package com.arun.springdynamodbdockerlocal.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.arun.springdynamodbdockerlocal.config.TokenLimitConfig;
import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.Tokens;
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
    public List<Token> getToken(String actorId, List<Tokens> tokens) {

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
            return null;
        }
    }

    private boolean validateActorEligible(List<Map<String, AttributeValue>> tokenItems, List<Integer> counts) {
        int tokenLimitFor24hr = Integer.parseInt(tokenLimitConfig.getTokenLimitFor24hr());
        int tokenLimitFor30day = Integer.parseInt(tokenLimitConfig.getTokenLimitFor30day());

        int size = tokenItems.size();
        for (Map<String, AttributeValue> tokens : tokenItems) {
            String duration = tokens.get("duration").getS();
            int count = Integer.parseInt(tokens.get("count").getN());

            if (duration.equals("24 hrs")) {
                int existingCountFor24Hr = counts.get(0) + count;
                counts.add(0, existingCountFor24Hr + size);
            } else if (duration.equals("30 days")) {
                int existingCountFor30Days = counts.get(1) + count;
                counts.add(1, existingCountFor30Days + size);
            }
        }

        if (counts.get(1) > tokenLimitFor30day) {
            return false;
        } else return counts.get(0) < tokenLimitFor24hr;
    }
}
