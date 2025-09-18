package com.first.challenge.r2dbc.repository;

import com.first.challenge.r2dbc.entity.ApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

// TODO: This file is just an example, you should delete or modify it
public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

    @Query("""
           SELECT a.application_id, a.amount, a.base_salary, a.term, a.email, a.state_id, a.loan_type_id
           FROM application a
           JOIN state s ON a.state_id = s.state_id
           WHERE a.email = :email AND s.name = 'APROBADO'
           """)
    Flux<ApplicationEntity> findApprovedByEmail(@Param("email") String email);
}
