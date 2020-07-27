package com.arun.springdynamodbdockerlocal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * @author arun on 7/26/20
 */

@Controller
@ControllerAdvice
public class TokenExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<CustomErrors> handleTokenException(TokenException e) {

        Errors errors = e.getErrors();

        HttpStatus httpStatus = e.getHttpStatus();
        List<ObjectError> allErrors;

        CustomErrors customErrors = new CustomErrors();
        if (errors != null) {
            allErrors = errors.getAllErrors();
            ObjectError objectError = allErrors.get(0);
            String field = "";
            if (objectError instanceof FieldError) {
                field = ((FieldError) objectError).getField();
            }

            customErrors.setErrorDescription(field + " : " + objectError.getDefaultMessage());
        } else {
            customErrors.setErrorDescription(e.getMessage());
        }

        customErrors.setError(httpStatus.getReasonPhrase());
        return new ResponseEntity<>(customErrors, httpStatus);
    }
}
