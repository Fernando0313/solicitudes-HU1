package com.first.challenge.r2dbc.adapter;


import com.first.challenge.model.auth.AuthenticatedUser;
import com.first.challenge.model.auth.gateways.JwtTokenGateway;
import com.first.challenge.model.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenAdapter implements JwtTokenGateway {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;

    public JwtTokenAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }



    @Override
    public Mono<AuthenticatedUser> validarToken(String token) {
        return Mono.fromCallable(() -> {
                    Claims claims = extraerClaims(token);

                    return AuthenticatedUser.builder()
                            .userId(claims.get("userId", String.class))
                            .email(claims.getSubject())
                            .firstName(claims.get("firstName", String.class))
                            .lastName(claims.get("lastName", String.class))
                            .roleId(claims.get("roleId", String.class))
                            .identityDocument(claims.get("identityDocument", String.class))
                            .permissions(List.of())
                            .build();
                })
                .onErrorMap(JwtException.class, e -> new InvalidTokenException("Token inválido o expirado"))
                .onErrorMap(IllegalArgumentException.class, e -> new InvalidTokenException("Token inválido o expirado"))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }
    @Override
    public Mono<String> extraerEmailDelToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = extraerClaims(token);
                return claims.getSubject();
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("No se pudo extraer el email del token: {}", e.getMessage());
                throw new InvalidTokenException("Token inválido");
            }
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

