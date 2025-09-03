package com.first.challenge.api;

import com.first.challenge.api.dto.LoanApplicationDto;
import com.first.challenge.api.mapper.ApplicationDtoMapper;
import com.first.challenge.consumer.RestConsumer;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.auth.gateways.JwtTokenGateway;
import com.first.challenge.model.exception.UnauthorizedAccessException;
import com.first.challenge.usecase.application.IApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final JwtTokenGateway jwtTokenGateway;

    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(AUTH_HEADER);
        String token = (authHeader != null && authHeader.startsWith(BEARER_PREFIX))
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;
        return jwtTokenGateway.validarToken(token) // 👈 validamos el token
                .flatMap(authenticatedUser -> serverRequest.bodyToMono(LoanApplicationDto.class)
                        .flatMap(dto -> {
                            // verificamos que coincida lo del token con lo del request
                            if (!authenticatedUser.getEmail().equals(dto.email()) ||
                                    !authenticatedUser.getIdentityDocument().equals(dto.identityDocument())) {
                                return Mono.error(new UnauthorizedAccessException("El usuario no coincide con el token"));
                            }

                            return restConsumer.getUserByIdentityDocument(dto.identityDocument(), dto.email(),token)
                                    .switchIfEmpty(Mono.error(new BusinessException("DOCUMENT_NOT_FOUND", "Usuario no encontrado en API externa")))
                                    .map(user -> dto);
                        })
                        .map(applicationDtoMapper::toEntity)
                        .flatMap(applicationUseCase::saveApplication)
                        .map(applicationDtoMapper::toSummary)
                        .flatMap(savedDto -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedDto))
                );
    }
}
