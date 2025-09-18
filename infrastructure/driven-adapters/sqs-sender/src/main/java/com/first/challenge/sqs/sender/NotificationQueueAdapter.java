package com.first.challenge.sqs.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.gateways.NotificationQueueGateway;
import com.first.challenge.sqs.sender.approvedApplications.ApprovedApplicationEvent;
import com.first.challenge.sqs.sender.debtcapacity.LoanEvaluationRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueAdapter implements NotificationQueueGateway {

    private final SqsAsyncClient sqsClient;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/445567089303/PRAGMASQS";
    private final String capacidadEndeudamiento = "https://sqs.us-east-1.amazonaws.com/445567089303/CAPACIDAD_ENDEUDAMIENTO_SQS";
    private final String approvedQueueUrl = "https://sqs.us-east-1.amazonaws.com/445567089303/solicitudes-aprobadas-queue";

    private final LoanEvaluationRequestMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendMessage(String email, String message) {
        return Mono.fromFuture(() ->
                sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(message)
                        .messageAttributes(Map.of(
                                "email", MessageAttributeValue.builder()
                                        .stringValue(email)
                                        .dataType("String")
                                        .build()
                        ))
                        .build()
                )
        ).then();
    }

    @Override
    public Mono<Void> sendLoanEvaluationRequest(Application application) {
        return mapper.toRequest(application)  // devuelve Mono<LoanEvaluationRequest>
                .doOnNext(req -> System.out.println("✅ Mapper OK: " + req))
                .doOnError(err -> System.err.println("❌ Error en mapper.toRequest: " + err.getMessage()))
                .flatMap(request ->
                        Mono.fromCallable(() -> objectMapper.writeValueAsString(request))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(body -> {
                                    SendMessageRequest messageRequest = SendMessageRequest.builder()
                                            .queueUrl(capacidadEndeudamiento)
                                            .messageBody(body)
                                            .build();

                                    return Mono.fromFuture(() -> sqsClient.sendMessage(messageRequest));
                                })
                )
                .doOnError(err -> System.err.println("❌ Error al enviar mensaje a SQS: " + err.getMessage()))
                .then();
    }

    @Override
    public Mono<Void> sendApprovedApplication(UUID applicationId, String status, BigDecimal amount) {
        return Mono.fromCallable(() -> {
                    ApprovedApplicationEvent event = ApprovedApplicationEvent.builder()
                            .applicationId(applicationId)
                            .status(status)
                            .amount(amount)
                            .contador(1)
                            .timestamp(Instant.now().toString())
                            .build();

                    return objectMapper.writeValueAsString(event);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(body -> {
                    SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                            .queueUrl(approvedQueueUrl)
                            .messageBody(body)
                            .build();

                    return Mono.fromFuture(() -> sqsClient.sendMessage(sendMsgRequest));
                })
                .doOnSuccess(resp -> log.info("✅ Evento enviado a SQS (aprobados): {}", resp.messageId()))
                .doOnError(err -> log.error("❌ Error al enviar evento a SQS (aprobados): {}", err.getMessage(), err))
                .then(); // 👉 asegura Mono<Void>
    }
}

