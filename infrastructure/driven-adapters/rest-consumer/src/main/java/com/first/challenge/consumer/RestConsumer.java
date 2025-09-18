package com.first.challenge.consumer;

import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.auth.gateways.Role;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestConsumer /* implements Gateway from domain */{
    private final WebClient client;
    private static final Logger logger = LoggerFactory.getLogger(RestConsumer.class);

    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<ObjectResponse> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

    public Mono<ObjectResponse> getUserByIdentityDocumentAndEmail(String identityDocument,String email,String bearerToken) {
        logger.info("Iniciando consulta externa de usuario con identityDocument={}", identityDocument);
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usuarios")
                        .queryParam("identityDocument", identityDocument)
                        .queryParam("email", email)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .retrieve()
                .onStatus(status -> status.value() == 204, response -> {
                    logger.warn("La API respondió 204 (No Content) para identityDocument={}", identityDocument);
                    // devolvemos un error controlado
                    return Mono.error(new BusinessException("USER_NOT_FOUND",
                            "No se encontró usuario con identityDocument=" + identityDocument));
                })
                .bodyToMono(ObjectResponse.class)
                .doOnSuccess(response -> {
                    if (response != null) {
                        logger.info(" Consulta externa exitosa para identityDocument={} -> {}",
                                identityDocument, response);
                    }
                })
                .doOnError(error -> {
                    logger.error("Error en consulta externa de usuario con identityDocument={}. Causa: {}",
                            identityDocument, error.getMessage(), error);
                });
    }

    public Mono<Role> findRoleById(UUID roleId, String bearerToken) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/roles/{id}")
                        .build(roleId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken) // 👈 se pasa aquí
                .retrieve()
                .bodyToMono(Role.class);
    }
}
