package com.arun.springdynamodbdockerlocal.controller;

import com.arun.springdynamodbdockerlocal.exception.TokenException;
import com.arun.springdynamodbdockerlocal.model.Token;
import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
import com.arun.springdynamodbdockerlocal.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author arun on 7/18/20
 */
@RestController
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/v1/token/{actorId}")
    public ResponseEntity<List<Token>> getToken(@PathVariable String actorId, @RequestBody List<TokenRequest> tokens) throws TokenException {
        List<Token> token = tokenService.getToken(actorId, tokens);
        return ResponseEntity.ok(token);
    }
}
