package com.arun.springdynamodbdockerlocal.repository;

import com.arun.springdynamodbdockerlocal.model.Token;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author arun on 7/18/20
 */

@Repository
public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {
}
