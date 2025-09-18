package com.first.challenge.usecase.loantype;

import com.first.challenge.model.loantype.LoanType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ILoanTypeUseCase {
    Mono<Boolean> existsById(UUID id);
    Mono<LoanType> findById(UUID id);
}
