package com.first.challenge.usecase.application;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.usecase.loantype.LoanTypeUseCase;
import com.first.challenge.usecase.state.StateUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ApplicationUseCase implements IApplicationUseCase{
    private final ApplicationRepository applicationRepository;
    private final LoanTypeUseCase loanTypeUseCase;
    private final StateUseCase stateUseCase;

    public Mono<Application> saveApplication(Application application) {
        return Mono.just(application)
                .flatMap(this::validateMandatory)
                .flatMap(this::validateFormats)
                .flatMap(validApp -> loanTypeUseCase.existsById(validApp.getLoanTypeId())
                        .flatMap(exists -> {
                            if (Boolean.FALSE.equals(exists)) {
                                return Mono.error(new BusinessException("INVALID_LOAN_TYPE",
                                        "El tipo de préstamo no existe"));
                            }
                            return stateUseCase.getStateByName("PENDIENTE")
                                    .switchIfEmpty(Mono.error(new BusinessException("STATE_NOT_FOUND",
                                            "No se encontró el estado PENDIENTE")) )
                                    .flatMap(state -> {
                                        validApp.setStateId(state.getStateId());
                                        return Mono.just(validApp);
                                    });
                        }))
                .flatMap(applicationRepository::save);
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
