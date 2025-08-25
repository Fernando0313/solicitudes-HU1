package com.first.challenge.r2dbc.adapter;


import com.first.challenge.model.state.State;
import com.first.challenge.model.state.gateways.StateRepository;
import com.first.challenge.r2dbc.entity.StateEntity;
import com.first.challenge.r2dbc.helper.ReactiveAdapterOperations;
import com.first.challenge.r2dbc.repository.StateReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class StateReactiveRepositoryAdapter  extends ReactiveAdapterOperations<
        State/* change for domain model */,
        StateEntity/* change for adapter model */,
        UUID,
        StateReactiveRepository
        > implements StateRepository {

    private static final Logger logger = LoggerFactory.getLogger(StateReactiveRepositoryAdapter.class);
    public StateReactiveRepositoryAdapter(StateReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.Map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, State.class/* change for domain model */));
    }


    @Override
    public Mono<State> findByName(String name) {
        return super.repository.findByName(name)
                .map(entity -> mapper.map(entity, State.class))
                .doOnSuccess(state -> logger.info("[StateReactiveRepositoryAdapter.findByName] name={} state.id={}", name,state.getStateId()))
                .doOnError(error -> logger.error("[StateReactiveRepositoryAdapter.findByName] Error al consultar name={} - Causa: {}", name, error.getMessage(), error));
    }
}
