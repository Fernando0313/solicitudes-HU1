package com.first.challenge.api;

import com.first.challenge.api.dto.LoanApplicationDto;
import com.first.challenge.api.mapper.ApplicationDtoMapper;
import com.first.challenge.consumer.RestConsumer;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.usecase.application.IApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private  final IApplicationUseCase applicationUseCase;
    private  final ApplicationDtoMapper applicationDtoMapper;
    private final RestConsumer restConsumer;
    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDto.class)
                .flatMap(dto -> {
                    if (dto.identityDocument() == null || dto.identityDocument().isBlank()) {
                        return Mono.error(new BusinessException("DOCUMENT_NOT_FOUND", "identityDocument es obligatorio"));
                    }
                    return restConsumer.getUserByIdentityDocument(dto.identityDocument())
                            .flatMap(externalUser -> Mono.just(dto))
                            .switchIfEmpty(Mono.error(new BusinessException("DOCUMENT_NOT_FOUND","Usuario no encontrado en API externa")));
                })
                .map(applicationDtoMapper::toEntity)
                .flatMap(applicationUseCase::saveApplication)
                .map(applicationDtoMapper::toSummary)
                .flatMap(savedDto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedDto));
    }
}
