package com.first.challenge.usecase.application;

import com.first.challenge.model.application.Application;
import reactor.core.publisher.Mono;

public interface IApplicationUseCase {
    Mono<Application> saveApplication(Application application);
}
