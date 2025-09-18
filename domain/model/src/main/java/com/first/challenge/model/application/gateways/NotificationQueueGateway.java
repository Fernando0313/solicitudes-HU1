package com.first.challenge.model.application.gateways;

import com.first.challenge.model.application.Application;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface NotificationQueueGateway {
    Mono<Void> sendMessage(String email, String message);
    Mono<Void> sendLoanEvaluationRequest(Application application);
    Mono<Void> sendApprovedApplication(UUID applicationId, String status, BigDecimal monto);
}
