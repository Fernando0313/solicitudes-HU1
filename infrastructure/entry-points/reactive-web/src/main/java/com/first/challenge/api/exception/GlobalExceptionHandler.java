package com.first.challenge.api.exception;

import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.exception.*;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {


    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request){
        Throwable error = getError(request);
        Map<String, Object> errorProperties = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = resolveHttpStatus(error);
        return  ServerResponse.status(status)//HttpStatus.INTERNAL_SERVER_ERROR
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorProperties));
    }

    private HttpStatus resolveHttpStatus(Throwable error) {
        if (error instanceof UnauthorizedAccessException) {
            return HttpStatus.FORBIDDEN; // 403
        } else if (error instanceof InvalidCredentialsException) {
            return HttpStatus.UNAUTHORIZED; // 401
        } else if (error instanceof BusinessException) {
            return HttpStatus.BAD_REQUEST; // 400
        }else if (error instanceof InvalidDataException) {
            return HttpStatus.BAD_REQUEST; // 400
        }else if (error instanceof InvalidTokenException) {
            return HttpStatus.BAD_REQUEST; // 400
        }else if (error instanceof UserAlreadyExistsException) {
            return HttpStatus.BAD_REQUEST; // 400
        } else if (error instanceof org.springframework.web.server.ServerWebInputException) {
            return HttpStatus.BAD_REQUEST; // 400
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // fallback
    }
}
