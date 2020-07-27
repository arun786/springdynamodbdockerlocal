package com.arun.springdynamodbdockerlocal.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

/**
 * @author arun on 7/26/20
 */

@Getter
@Setter
public class TokenException extends Exception {
    private HttpStatus httpStatus;
    private String message;
    private Errors errors;

    public TokenException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public TokenException(HttpStatus httpStatus, Errors errors) {
        this.httpStatus = httpStatus;
        this.errors = errors;
    }
}
