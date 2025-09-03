package com.first.challenge.api.exception;

import com.first.challenge.model.application.exceptions.BusinessException;
import com.first.challenge.model.exception.InvalidCredentialsException;
import com.first.challenge.model.exception.InvalidTokenException;
import com.first.challenge.model.exception.UnauthorizedAccessException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes =  new LinkedHashMap<>();
        Throwable error = getError(request);
        boolean isControlledError = isControlledError(error);

        if (isControlledError) {
            // Error controlado: mostrar mensaje específico
            errorAttributes.put("message", error.getMessage());
            errorAttributes.put("errorType", error.getClass().getSimpleName());
        } else {
            // Error no controlado: mensaje genérico
            errorAttributes.put("message", "Ocurrió un error inesperado. Por favor, contacte al administrador.");
            errorAttributes.put("errorType", "InternalServerError");
        }

        errorAttributes.put("path",request.path());
        return errorAttributes;
    }

    private boolean isControlledError(Throwable error) {
        // Lista de errores considerados como controlados
        return  error instanceof BusinessException || error instanceof InvalidCredentialsException || error instanceof UnauthorizedAccessException
                || error instanceof InvalidTokenException ||
                error instanceof org.springframework.web.server.ServerWebInputException;
    }

}

