package com.first.challenge.model.auth.gateways;

import com.first.challenge.model.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

public interface JwtTokenGateway {
    Mono<AuthenticatedUser> validarToken(String token);
    Mono<String> extraerEmailDelToken(String token);

}

