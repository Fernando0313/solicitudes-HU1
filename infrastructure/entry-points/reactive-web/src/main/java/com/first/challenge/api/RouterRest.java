package com.first.challenge.api;

import com.first.challenge.api.config.ApplicationPath;
import com.first.challenge.api.dto.LoanApplicationDto;
import com.first.challenge.api.dto.LoanApplicationResponseDto;
import com.first.challenge.api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@RequiredArgsConstructor
@Configuration
@Tag(name = "Solicitud de Prestamo", description = "Operaciones relacionadas con solicitudes de préstamos")
public class RouterRest {
    private final ApplicationPath applicationPath;

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitudes", // <-- ajusta según tu ApplicationPath
                    beanClass = Handler.class,
                    beanMethod = "listenSaveApplication",
                    operation = @Operation(
                            operationId = "saveLoanApplication",
                            summary = "Registrar una nueva solicitud de préstamo",
                            description = "Crea una nueva solicitud de préstamo con los datos enviados en el cuerpo.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = LoanApplicationDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud registrada exitosamente",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = LoanApplicationResponseDto.class)
                                            )),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            ))
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(applicationPath.getSolicitud()), handler::listenSaveApplication);
    }
}
