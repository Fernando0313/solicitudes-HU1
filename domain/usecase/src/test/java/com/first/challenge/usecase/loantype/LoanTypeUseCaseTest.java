package com.first.challenge.usecase.loantype;

import com.first.challenge.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class LoanTypeUseCaseTest {

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @InjectMocks
    private LoanTypeUseCase loanTypeUseCase;

    private UUID loanTypeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanTypeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("existsById should return true when loan type exists")
    void existsById_WhenLoanTypeExists_ShouldReturnTrue() {
        when(loanTypeRepository.existsById(loanTypeId)).thenReturn(Mono.just(true));

        StepVerifier.create(loanTypeUseCase.existsById(loanTypeId))
                .expectNext(true)
                .verifyComplete();

        verify(loanTypeRepository, times(1)).existsById(loanTypeId);
    }

    @Test
    @DisplayName("existsById should return false when loan type does not exist")
    void existsById_WhenLoanTypeDoesNotExist_ShouldReturnFalse() {
        when(loanTypeRepository.existsById(loanTypeId)).thenReturn(Mono.just(false));

        StepVerifier.create(loanTypeUseCase.existsById(loanTypeId))
                .expectNext(false)
                .verifyComplete();

        verify(loanTypeRepository, times(1)).existsById(loanTypeId);
    }

    @Test
    @DisplayName("existsById should propagate error when repository fails")
    void existsById_WhenRepositoryThrowsError_ShouldPropagateError() {
        RuntimeException ex = new RuntimeException("DB error");
        when(loanTypeRepository.existsById(loanTypeId)).thenReturn(Mono.error(ex));

        StepVerifier.create(loanTypeUseCase.existsById(loanTypeId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();

        verify(loanTypeRepository, times(1)).existsById(loanTypeId);
    }
}