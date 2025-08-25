package com.first.challenge.model.application.gateways;

import com.first.challenge.model.application.Application;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Mono<Application> save(Application application);
}
