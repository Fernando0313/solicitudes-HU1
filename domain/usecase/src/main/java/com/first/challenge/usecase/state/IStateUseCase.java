package com.first.challenge.usecase.state;

import com.first.challenge.model.state.State;
import reactor.core.publisher.Mono;

public interface IStateUseCase {
    Mono<State> getStateByName(String name);
}
