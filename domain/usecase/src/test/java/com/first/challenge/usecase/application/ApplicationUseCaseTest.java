package com.first.challenge.usecase.application;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.model.state.State;
import com.first.challenge.usecase.loantype.LoanTypeUseCase;
import com.first.challenge.usecase.state.StateUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private LoanTypeUseCase loanTypeUseCase;

    @Mock
    private StateUseCase stateUseCase;

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    private Application validApplication;
    private UUID loanTypeId;
    private UUID stateId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loanTypeId = UUID.randomUUID();
        stateId = UUID.randomUUID();

        validApplication = new Application();
        validApplication.setApplicationId(UUID.randomUUID());
        validApplication.setAmount(BigDecimal.valueOf(10000));
        validApplication.setTerm(12);
        validApplication.setEmail("test@example.com");
        validApplication.setLoanTypeId(loanTypeId);
    }

    @Test
    @DisplayName("saveApplication should save when valid application")
    void saveApplication_WhenValid_ShouldSave() {
        State state = new State(stateId, "PENDIENTE","Estado inicial de la solicitud de préstamo"); // asumiendo constructor
        when(loanTypeUseCase.existsById(loanTypeId)).thenReturn(Mono.just(true));
        when(stateUseCase.getStateByName("PENDIENTE")).thenReturn(Mono.just(state));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectNextMatches(app -> app.getStateId().equals(stateId))
                .verifyComplete();

        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("should fail when amount is missing")
    void saveApplication_WhenAmountMissing_ShouldFail() {
        validApplication.setAmount(null);

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getCode().equals("MANDATORY_FIELD"))
                .verify();

        verifyNoInteractions(loanTypeUseCase, stateUseCase, applicationRepository);
    }

    @Test
    @DisplayName("should fail when email format is invalid")
    void saveApplication_WhenInvalidEmail_ShouldFail() {
        validApplication.setEmail("invalid-email");

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getCode().equals("INVALID_EMAIL"))
                .verify();

        verifyNoInteractions(loanTypeUseCase, stateUseCase, applicationRepository);
    }

    @Test
    @DisplayName("should fail when loan type does not exist")
    void saveApplication_WhenLoanTypeNotExists_ShouldFail() {
        when(loanTypeUseCase.existsById(loanTypeId)).thenReturn(Mono.just(false));

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getCode().equals("INVALID_LOAN_TYPE"))
                .verify();

        verifyNoInteractions(applicationRepository);
    }

    @Test
    @DisplayName("should fail when state PENDIENTE not found")
    void saveApplication_WhenStateNotFound_ShouldFail() {
        when(loanTypeUseCase.existsById(loanTypeId)).thenReturn(Mono.just(true));
        when(stateUseCase.getStateByName("PENDIENTE")).thenReturn(Mono.empty());

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getCode().equals("STATE_NOT_FOUND"))
                .verify();

        verifyNoInteractions(applicationRepository);
    }

    @Test
    @DisplayName("should propagate error from repository")
    void saveApplication_WhenRepositoryFails_ShouldPropagateError() {
        State state = new State(stateId, "PENDIENTE","Estado inicial de la solicitud de préstamo");
        when(loanTypeUseCase.existsById(loanTypeId)).thenReturn(Mono.just(true));
        when(stateUseCase.getStateByName("PENDIENTE")).thenReturn(Mono.just(state));
        when(applicationRepository.save(any(Application.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(applicationUseCase.saveApplication(validApplication))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();
    }
}
