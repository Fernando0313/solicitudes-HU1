package com.first.challenge.r2dbc.repository;

import com.first.challenge.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, UUID>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
    Mono<Boolean> existsById(UUID id);
}