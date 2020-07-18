package com.arun.springdynamodbdockerlocal.config;

import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author arun on 7/18/20
 */
@Component
public class TokenLoader implements CommandLineRunner {

    private final TokenRepository tokenRepository;
    private final TokenConfig tokenConfig;

    @Autowired
    public TokenLoader(TokenRepository tokenRepository, TokenConfig tokenConfig) {
        this.tokenRepository = tokenRepository;
        this.tokenConfig = tokenConfig;
    }

    @Override
    public void run(String... args) {

        int uuid = Integer.parseInt(tokenConfig.getUuid());
        int tokens = Integer.parseInt(tokenConfig.getTokens());
        for (int i = 0; i < uuid; i++) {
            for (int j = 0; j < tokens; j++) {
                int number = (int) Math.floor(Math.random() * 100000);
                Token token = Token.builder().token(String.valueOf(number)).uuid(UUID.randomUUID().toString()).build();
                tokenRepository.save(token);
            }
        }
    }
}
