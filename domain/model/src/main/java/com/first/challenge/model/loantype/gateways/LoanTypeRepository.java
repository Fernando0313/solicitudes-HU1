package com.first.challenge.model.loantype.gateways;

import com.first.challenge.model.loantype.LoanType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(UUID id);
}
