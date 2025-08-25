package com.first.challenge.usecase.loantype;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ILoanTypeUseCase {
    Mono<Boolean> existsById(UUID id);
}
