package com.first.challenge.r2dbc.adapter;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.r2dbc.repository.ApplicationReactiveRepository;
import com.first.challenge.r2dbc.entity.ApplicationEntity;
import com.first.challenge.r2dbc.helper.ReactiveAdapterOperations;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        UUID,
        ApplicationReactiveRepository
> implements ApplicationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationReactiveRepositoryAdapter.class);
    private final TransactionalOperator operadorTransaccional;
    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper,
                                                TransactionalOperator operadorTransaccional) {
        super(repository, mapper, d -> mapper.map(d, Application.class/* change for domain model */));

        this.operadorTransaccional = operadorTransaccional;
    }


    @Override
    public Mono<Application> save(Application application) {
        logger.info("[ApplicationReactiveRepositoryAdapter] Guardando solicitud email={}", application.getEmail());
        return  super.save(application)
                .doOnSuccess(saved -> logger.info("[ApplicationReactiveRepositoryAdapter.save] solicitud registrada id={} email={}", saved.getApplicationId(), saved.getEmail()))
                .doOnError(error -> logger.error("[ApplicationReactiveRepositoryAdapter.save] Error solicitud email={} - Causa: {}", application.getEmail(), error.getMessage(), error))
                .as(operadorTransaccional::transactional)
                .doFinally(signal -> logger.info("[ApplicationReactiveRepositoryAdapter.save] Finalizó flujo con señal={}", signal));
    }
}
