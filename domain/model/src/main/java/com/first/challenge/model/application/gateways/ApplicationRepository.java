package com.first.challenge.model.application.gateways;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.dto.PendingDecisionResponse;
import com.first.challenge.model.criteria.SearchCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository {
    Mono<Application> save(Application application);
    Mono<Application> updateState(Application application);
    Mono<Application> findById(UUID id);
    Flux<PendingDecisionResponse> findByCriteria(SearchCriteria criteria);
    Mono<Long> countByCriteria(SearchCriteria criteria);
    Flux<Application> findApprovedByEmail(String identityDocument);


}
