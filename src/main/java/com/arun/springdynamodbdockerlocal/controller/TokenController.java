package com.arun.springdynamodbdockerlocal.controller;

import com.arun.springdynamodbdockerlocal.repository.TokenValidationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<HttpStatus> getToken(@PathVariable String actorId) {
        tokenValidationRepository.getToken(actorId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

}
