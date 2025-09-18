package com.first.challenge.model.secret.gateways;

import reactor.core.publisher.Mono;
import java.util.Map;

public interface SecretRepository {
    Mono<Map<String, String>> getSecret(String secretName);
}
