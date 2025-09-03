package com.first.challenge.consumer;


import com.first.challenge.model.application.exceptions.BusinessException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;
import java.io.IOException;


class RestConsumerTest {

    private static RestConsumer restConsumer;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate getUserByIdentityDocument when response is 200 OK.")
    void validateGetUserByIdentityDocumentSuccess() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"exists\" : true}")); // cambia el JSON al campo correcto

        var response = restConsumer.getUserByIdentityDocument("12345678","email","");

        StepVerifier.create(response)
                .expectNextMatches(objectResponse -> objectResponse.isExists()) // accede a exists
                .verifyComplete();
    }


    @Test
    @DisplayName("Validate getUserByIdentityDocument when response is 204 No Content.")
    void validateGetUserByIdentityDocumentNoContent() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NO_CONTENT.value()));

        var response = restConsumer.getUserByIdentityDocument("12345678","email","");

        StepVerifier.create(response)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getCode().equals("USER_NOT_FOUND"))
                .verify();
    }

    @Test
    @DisplayName("Validate getUserByIdentityDocument when API returns 500.")
    void validateGetUserByIdentityDocumentServerError() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        var response = restConsumer.getUserByIdentityDocument("12345678","email","");

        StepVerifier.create(response)
                .expectError(WebClientResponseException.class)
                .verify();
    }
}