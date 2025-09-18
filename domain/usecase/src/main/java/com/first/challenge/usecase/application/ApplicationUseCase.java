package com.first.challenge.usecase.application;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.dto.PendingDecisionResponse;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.model.application.gateways.NotificationQueueGateway;
import com.first.challenge.model.criteria.PageResponse;
import com.first.challenge.model.criteria.SearchCriteria;
import com.first.challenge.usecase.loantype.LoanTypeUseCase;
import com.first.challenge.usecase.state.StateUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ApplicationUseCase implements IApplicationUseCase{
    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;
    private final StateUseCase stateUseCase;
    private final NotificationQueueGateway notificationQueueGateway;

    @Override
    public Mono<Application> saveApplication(Application application) {
        return Mono.just(application)
                .flatMap(this::validateMandatory)
                .flatMap(this::validateFormats)
                .flatMap(validApp -> loanTypeUseCase.findById(validApp.getLoanTypeId())
                        .switchIfEmpty(Mono.error(new BusinessException("INVALID_LOAN_TYPE",
                                "El tipo de préstamo no existe")))
                        .flatMap(loanType -> stateUseCase.getStateByName("PENDIENTE")
                                .switchIfEmpty(Mono.error(new BusinessException("STATE_NOT_FOUND",
                                        "No se encontró el estado PENDIENTE")))
                                .flatMap(state -> {
                                    validApp.setStateId(state.getStateId());
                                    return applicationRepository.save(validApp)
                                            .flatMap(savedApp -> {
                                                if (Boolean.TRUE.equals(loanType.getAutomaticValidation())) {
                                                    return notificationQueueGateway
                                                            .sendLoanEvaluationRequest(savedApp)
                                                            .thenReturn(savedApp);
                                                }
                                                return Mono.just(savedApp);
                                            });
                                })
                        )
                );
    }



    @Override
    public Mono<PageResponse<PendingDecisionResponse>> execute(SearchCriteria c) {
        return applicationRepository.findByCriteria(c)
                .collectList()
                .zipWith(applicationRepository.countByCriteria(c))
                .map(t -> PageResponse.of(t.getT1(), c.getPage(), c.getSize(), t.getT2()));
    }

    @Override
    public Mono<Application> updateState(UUID id, String stateName) {
        return Mono.justOrEmpty(id)
                .switchIfEmpty(Mono.error(new BusinessException("INVALID_ID", "El id de la aplicación no puede ser nulo")))
                .flatMap(appId -> {
                    if (stateName == null || stateName.isBlank()) {
                        return Mono.error(new BusinessException("INVALID_STATE", "El nombre del estado no puede estar vacío"));
                    }
                    return stateUseCase.getStateByName(stateName)
                            .switchIfEmpty(Mono.error(new BusinessException("STATE_NOT_FOUND",
                                    "No se encontró el estado: " + stateName)))
                            .flatMap(state -> applicationRepository.findById(appId)
                                    .switchIfEmpty(Mono.error(new BusinessException("APPLICATION_NOT_FOUND",
                                            "No se encontró la aplicación con id: " + appId)))
                                    .flatMap(application -> {
                                        application.setStateId(state.getStateId());

                                        return applicationRepository.updateState(application)
                                                .flatMap(updatedApp -> {
                                                    // primero enviamos la notificación normal
                                                    Mono<Application> notify = notificationQueueGateway.sendMessage(
                                                            updatedApp.getEmail(),
                                                            "La solicitud " + updatedApp.getApplicationId() +
                                                                    " cambió a estado: " + stateName
                                                    ).thenReturn(updatedApp);

                                                    // si el estado es APROBADO, también enviamos a la cola de aprobados
                                                    if ("APROBADO".equalsIgnoreCase(stateName)) {
                                                        return notify.flatMap(app ->
                                                                notificationQueueGateway.sendApprovedApplication(
                                                                        app.getApplicationId(),
                                                                        stateName,
                                                                        updatedApp.getAmount() // suponiendo que Application tiene getMonto()
                                                                ).thenReturn(app)
                                                        );
                                                    }

                                                    return notify;
                                                });
                                    }));
                });
    }


    @Override
    public Mono<Application> findById(UUID id) {
        return applicationRepository.findById(id);
    }

    @Override
    public Flux<Application> findApprovedByEmail(String email) {
        return applicationRepository.findApprovedByEmail(email);
    }


    private Mono<Application> validateMandatory(Application application) {
        if (application.getAmount() == null) {
            return Mono.error(new BusinessException("MANDATORY_FIELD", "El campo amount es obligatorio"));
        }
        if (application.getTerm() == null) {
            return Mono.error(new BusinessException("MANDATORY_FIELD", "El campo term es obligatorio"));
        }
        if (application.getEmail() == null || application.getEmail().isBlank()) {
            return Mono.error(new BusinessException("MANDATORY_FIELD", "El campo email es obligatorio"));
        }
        if (application.getLoanTypeId() == null) {
            return Mono.error(new BusinessException("MANDATORY_FIELD", "El campo loanTypeId es obligatorio"));
        }
        return Mono.just(application);
    }

    private Mono<Application> validateFormats(Application application) {
            if (!EMAIL_PATTERN.matcher(application.getEmail()).matches()) {
            return Mono.error(new BusinessException("INVALID_EMAIL", "Formato de correo electrónico inválido"));
        }
        return Mono.just(application);
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
}
