package com.first.challenge.model.application.gateways;

import reactor.core.publisher.Mono;

public interface NotificationQueueGateway {
    Mono<Void> sendMessage(String email, String message);
}
