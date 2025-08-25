package com.first.challenge.usecase.state;

import com.first.challenge.model.state.State;
import com.first.challenge.model.state.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class StateUseCaseTest {

    @Mock
    private StateRepository stateRepository;

    private StateUseCase stateUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stateUseCase = new StateUseCase(stateRepository);
    }

    @Test
    void getStateByName_ReturnsState_WhenExists() {
        String name = "PENDIENTE";
        State mockState = new State(UUID.fromString("dc199ce1-c56c-430d-ada8-d1adac863c52"),
                "PENDIENTE","Estado inicial de la solicitud de préstamo");
        when(stateRepository.findByName(name)).thenReturn(Mono.just(mockState));

        Mono<State> result = stateUseCase.getStateByName(name);

        StepVerifier.create(result)
                .expectNextMatches(state -> state.getName().equals("PENDIENTE"))
                .verifyComplete();

        verify(stateRepository).findByName(name);
    }

    @Test
    void getStateByName_ReturnsEmpty_WhenNotExists() {
        String name = "FINALIZADO";
        when(stateRepository.findByName(name)).thenReturn(Mono.empty());

        Mono<State> result = stateUseCase.getStateByName(name);

        StepVerifier.create(result)
                .verifyComplete();

        verify(stateRepository).findByName(name);
    }

}
