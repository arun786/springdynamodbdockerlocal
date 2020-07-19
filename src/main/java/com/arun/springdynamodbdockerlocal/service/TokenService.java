package com.arun.springdynamodbdockerlocal.service;

import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.Tokens;

import java.util.List;

/**
 * @author arun on 7/18/20
 */
public interface TokenService {
    List<Token> getToken(String actorId, List<Tokens> tokens);
}
