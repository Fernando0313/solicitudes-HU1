package com.first.challenge.r2dbc.adapter;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.r2dbc.repository.ApplicationReactiveRepository;
import com.first.challenge.r2dbc.entity.ApplicationEntity;
import com.first.challenge.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Application/* change for domain model */,
        ApplicationEntity/* change for adapter model */,
        UUID,
        ApplicationReactiveRepository
> implements ApplicationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationReactiveRepositoryAdapter.class);
    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.Map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Application.class/* change for domain model */));
    }

    @Transactional
    @Override
    public Mono<Application> save(Application application) {
        logger.info("[ApplicationReactiveRepositoryAdapter] Guardando solicitud email={}", application.getEmail());
        return super.save(application)
                .doOnSuccess(saved -> logger.info("[ApplicationReactiveRepositoryAdapter.save] solicitud registrada id={} email={}", saved.getApplicationId(), saved.getEmail()))
                .doOnError(error -> logger.error("[ApplicationReactiveRepositoryAdapter.save] Error solicitud email={} - Causa: {}", application.getEmail(), error.getMessage(), error))
                .doFinally(signal -> logger.info("[ApplicationReactiveRepositoryAdapter.save] Finalizó flujo con señal={}", signal));
    }
}
