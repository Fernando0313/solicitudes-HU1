package com.first.challenge.secretsmanager.adapter;


import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import com.first.challenge.model.secret.SecretModel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecretsAdapter {

    private final GenericManagerAsync secretManager;

    @Value("${aws.secretName}")
    private String secretName;

    @SneakyThrows
    public Mono<SecretModel> getSecrets() {
        return secretManager.getSecret(secretName, SecretModel.class);
    }
}