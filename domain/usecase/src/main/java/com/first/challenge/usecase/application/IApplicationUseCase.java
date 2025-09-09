package com.first.challenge.usecase.application;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.dto.PendingDecisionResponse;
import com.first.challenge.model.criteria.PageResponse;
import com.first.challenge.model.criteria.SearchCriteria;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IApplicationUseCase {
    Mono<Application> saveApplication(Application application);
    Mono<PageResponse<PendingDecisionResponse>> execute(SearchCriteria c);
    Mono<Application> updateState(UUID id, String estate);
    Mono<Application> findById(UUID id);
}
