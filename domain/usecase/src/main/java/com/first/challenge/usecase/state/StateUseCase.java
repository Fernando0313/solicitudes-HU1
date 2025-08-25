package com.first.challenge.usecase.state;

import com.first.challenge.model.loantype.LoanType;
import com.first.challenge.model.state.State;
import com.first.challenge.model.state.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class StateUseCase implements IStateUseCase{
    private final StateRepository stateRepository;

    public Mono<State> getStateByName(String name){
        return stateRepository.findByName(name);
    }
}
