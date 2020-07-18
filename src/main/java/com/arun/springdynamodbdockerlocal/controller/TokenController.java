package com.arun.springdynamodbdockerlocal.controller;

import com.arun.springdynamodbdockerlocal.model.request.TokenRequest;
import com.arun.springdynamodbdockerlocal.repository.TokenValidationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author arun on 7/18/20
 */
@RestController
public class TokenController {

    private TokenValidationRepository tokenValidationRepository;

    public TokenController(TokenValidationRepository tokenValidationRepository) {
        this.tokenValidationRepository = tokenValidationRepository;
    }

    @GetMapping("/v1/token/{actorId}")
    public ResponseEntity<HttpStatus> getToken(@PathVariable String actorId, @RequestBody List<TokenRequest> tokenRequests) {
        tokenValidationRepository.getToken(actorId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

}
