package com.first.challenge.model.state.gateways;

import com.first.challenge.model.loantype.LoanType;
import com.first.challenge.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository {
    Mono<State> findByName(String name);
}
