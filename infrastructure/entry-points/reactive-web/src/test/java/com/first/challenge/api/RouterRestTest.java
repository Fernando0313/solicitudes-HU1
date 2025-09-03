package com.first.challenge.api;

import com.first.challenge.api.config.ApplicationPath;
import com.first.challenge.api.dto.LoanApplicationDto;
import com.first.challenge.api.dto.LoanApplicationResponseDto;
import com.first.challenge.api.mapper.ApplicationDtoMapper;
import com.first.challenge.consumer.ObjectResponse;
import com.first.challenge.consumer.RestConsumer;
import com.first.challenge.model.application.Application;
import com.first.challenge.model.state.State;
import com.first.challenge.usecase.application.ApplicationUseCase;
import com.first.challenge.usecase.state.StateUseCase;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@Import(com.first.challenge.api.exception.GlobalControllerAdvice.class)
@EnableConfigurationProperties(ApplicationPath.class)
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplicationUseCase applicationUseCase;
    @MockitoBean
    private StateUseCase stateUseCase;

    @MockitoBean
    private RestConsumer restConsumer;

    @MockitoBean
    private ApplicationDtoMapper applicationDtoMapper;
    

    @BeforeEach
    void setUp() {
        // Stub del RestConsumer
        when(restConsumer.getUserByIdentityDocument(anyString(),"",""))
                .thenReturn(Mono.just(ObjectResponse.builder().exists(true).build()));
        when(stateUseCase.getStateByName("PENDIENTE"))
                .thenReturn(Mono.just(new State(UUID.randomUUID(), "PENDIENTE","test")));
//         Stub del use case
        when(applicationUseCase.saveApplication(any()))
                .thenReturn(Mono.just(applicationEntityOne));
//
//        // Stub del mapper
        when(applicationDtoMapper.toEntity(any(LoanApplicationDto.class)))
                .thenReturn(applicationEntityOne);
        when(applicationDtoMapper.toSummary(any()))
                .thenReturn(responseOne);
    }
    private final String solicitud = "/api/v1/solicitud";
    private final String solicitudById = "/api/v1/solicitud";

    private final Application applicationEntityOne = Application.builder()

            .amount(BigDecimal.valueOf(500.00))
            .term(12)
            .email("user@test.com")
            .loanTypeId(UUID.fromString("523b3307-7d27-4165-b942-5cd7dbbc328d"))
            .build();
    private final LoanApplicationDto applicationOne = LoanApplicationDto.builder()

            .amount(BigDecimal.valueOf(500.00))
            .term(12)
            .email("user@test.com")
            .identityDocument("12345678")
            .loanTypeId("523b3307-7d27-4165-b942-5cd7dbbc328d")
            .build();
    private final LoanApplicationResponseDto responseOne = LoanApplicationResponseDto.builder()
            .amount(BigDecimal.valueOf(500.00))
            .term(12)
            .email("user@test.com")
            .loanTypeId(UUID.fromString("523b3307-7d27-4165-b942-5cd7dbbc328d"))
            .stateId(UUID.fromString("523b3307-7d27-4165-b942-5cd7dbbc328d"))
            .build();
    @Autowired
    private ApplicationPath applicationPath;
    @PostConstruct
    public void logPaths() {
        System.out.println("Solicitud path: " + applicationPath.getSolicitud());
    }
    @Test
    void shouldLoadApplicationPathProperties() {
        assertEquals("/api/v1/solicitud", applicationPath.getSolicitud());
        assertEquals("/api/v1/solicitud/{id}", applicationPath.getSolicitudById());
    }


@Test
void shouldPostSaveApplication() {

    when(applicationUseCase.saveApplication(any(Application.class)))
            .thenReturn(Mono.just(applicationEntityOne));

    webTestClient.post()
            .uri("/api/v1/solicitud") // ajusta según tu ruta
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(applicationOne)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(LoanApplicationResponseDto.class)
            .value(responseOne -> {
                Assertions.assertThat(responseOne.email()).isEqualTo(applicationOne.email());
                Assertions.assertThat(responseOne.amount()).isEqualByComparingTo(applicationOne.amount());
            });
}

    @Test
    void shouldReturnBadRequestWhenIdentityMissing() {
        LoanApplicationDto invalid = LoanApplicationDto.builder()
                .amount(BigDecimal.valueOf(300.00))
                .term(6)
                .email("user@test.com")
                .loanTypeId("523b3307-7d27-4165-b942-5cd7dbbc328d")
                .identityDocument("")
                .build();

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenExternalUserNotFound() {
        when(restConsumer.getUserByIdentityDocument(anyString(),"","")).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(applicationOne)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
