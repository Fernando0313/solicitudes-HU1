package com.first.challenge.api.security;


import com.first.challenge.consumer.RestConsumer;
import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.auth.AuthenticatedUser;
import com.first.challenge.model.auth.gateways.Role;
import com.first.challenge.model.exception.InvalidTokenException;
import com.first.challenge.usecase.auth.ValidateTokenUseCase;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final ValidateTokenUseCase validateTokenUseCase;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    //private final IRoleUseCase roleUseCase;
    private final RestConsumer restConsumer;
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        return extractToken(exchange)
                .flatMap(this::authenticateToken)
                .flatMap(authentication -> {
                    SecurityContext securityContext = new SecurityContextImpl(authentication);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.defer(() -> Mono.just(securityContext)))); //Mono.just(securityContext)));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            return Mono.just(token);
        }

        return Mono.empty();
    }
    private Mono<UsernamePasswordAuthenticationToken> authenticateToken(String token) {
        return validateTokenUseCase.ejecutar(token)
                .flatMap(usuario->createAuthentication(usuario,token))
                .doOnError(error ->
                        log.debug("Error al validar token: {}", error.getMessage()));
    }


    private Mono<UsernamePasswordAuthenticationToken> createAuthentication(AuthenticatedUser usuario, String token) {
        return getRoleName(usuario.getRoleId(),token)
                .map(roleName -> {
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + roleName)
                    );

                    return new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            authorities
                    );
                });
    }



    private Mono<String> getRoleName(String roleId,String token) {
        return restConsumer.findRoleById(UUID.fromString(roleId),token)
                .map(Role::getName)
                .switchIfEmpty(Mono.error(new BusinessException( "ROLE_NOTFOUND","Rol no encontrado con id: " + roleId)))
                .onErrorResume(error -> Mono.error(new InvalidTokenException("Token inválido o expirado")));
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/v1/login") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars") ||
                path.equals("/swagger-ui.html");
    }
}
