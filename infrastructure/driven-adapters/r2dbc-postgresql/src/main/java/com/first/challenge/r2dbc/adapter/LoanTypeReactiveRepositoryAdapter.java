package com.first.challenge.r2dbc.adapter;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.model.loantype.LoanType;
import com.first.challenge.model.loantype.gateways.LoanTypeRepository;
import com.first.challenge.model.state.State;
import com.first.challenge.r2dbc.entity.ApplicationEntity;
import com.first.challenge.r2dbc.entity.LoanTypeEntity;
import com.first.challenge.r2dbc.helper.ReactiveAdapterOperations;
import com.first.challenge.r2dbc.repository.ApplicationReactiveRepository;
import com.first.challenge.r2dbc.repository.LoanTypeReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class LoanTypeReactiveRepositoryAdapter  extends ReactiveAdapterOperations<
        LoanType/* change for domain model */,
        LoanTypeEntity/* change for adapter model */,
        UUID,
        LoanTypeReactiveRepository
        > implements LoanTypeRepository {

    private static final Logger logger = LoggerFactory.getLogger(LoanTypeReactiveRepositoryAdapter.class);
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.Map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, LoanType.class/* change for domain model */));
    }


    @Override
    public Mono<Boolean> existsById(UUID id) {
        return super.repository.existsById(id)
                .doOnSuccess(exists -> logger.info("[LoanTypeReactiveRepositoryAdapter.existsById] el id={} existe={}", id, exists))
                .doOnError(error -> logger.error("[LoanTypeReactiveRepositoryAdapter.existsById] Error al consultar id={} - Causa: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<LoanType> findById(UUID id) {
        return super.findById(id)
                .map(entity -> mapper.map(entity, LoanType.class))
                .doOnSuccess(loanType -> {
                    if (loanType != null) {
                        logger.info("[LoanTypeReactiveRepositoryAdapter.findById] Se encontró LoanType con id={}", id);
                    } else {
                        logger.info("[LoanTypeReactiveRepositoryAdapter.findById] No existe LoanType con id={}", id);
                    }
                })
                .doOnError(error -> logger.error(
                        "[LoanTypeReactiveRepositoryAdapter.findById] Error al consultar id={} - Causa: {}",
                        id,
                        error.getMessage(),
                        error
                ));
    }

}
