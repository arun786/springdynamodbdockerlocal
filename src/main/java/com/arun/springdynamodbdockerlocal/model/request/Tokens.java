package com.arun.springdynamodbdockerlocal.model.request;

import lombok.*;

/**
 * @author arun on 7/18/20
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tokens {
    private String token;
}
