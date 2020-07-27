package com.arun.springdynamodbdockerlocal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author arun on 7/26/20
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrors {
    private String error;
    private String errorDescription;
}
