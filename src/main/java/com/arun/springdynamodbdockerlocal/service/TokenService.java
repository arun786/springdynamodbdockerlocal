package com.arun.springdynamodbdockerlocal.service;

import com.arun.springdynamodbdockerlocal.exception.TokenException;
import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;

import java.util.List;

/**
 * @author arun on 7/18/20
 */
public interface TokenService {
    List<Token> getToken(String actorId, List<TokenRequest> tokens) throws TokenException;
}
