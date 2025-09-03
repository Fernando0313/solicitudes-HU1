package com.first.challenge.usecase.auth;

import com.first.challenge.model.auth.AuthenticatedUser;
import com.first.challenge.model.auth.gateways.JwtTokenGateway;
import com.first.challenge.model.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidateTokenUseCase {
    private final JwtTokenGateway jwtTokenGateway;

    public Mono<AuthenticatedUser> ejecutar(String token) {
        return jwtTokenGateway.validarToken(token)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Token inválido o expirado")));
    }
}

