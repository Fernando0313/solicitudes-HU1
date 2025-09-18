package com.first.challenge.usecase.loantype;

import com.first.challenge.model.loantype.LoanType;
import com.first.challenge.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class LoanTypeUseCase implements  ILoanTypeUseCase{
    private final LoanTypeRepository loanTypeRepository;

    public Mono<Boolean> existsById(UUID id){
        return loanTypeRepository.existsById(id);
    }

    @Override
    public Mono<LoanType> findById(UUID id) {
        return loanTypeRepository.findById(id);
    }
}
