package com.first.challenge.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Solicitud de préstamo")
@Builder()
public record LoanApplicationDto(

        @Schema(
                description = "Monto solicitado para el préstamo",
                example = "15000.50",
                required = true
        )
        BigDecimal amount,

        @Schema(
                description = "Plazo del préstamo en meses",
                example = "24",
                required = true
        )
        Integer term,

        @Schema(
                description = "Correo electrónico del solicitante",
                example = "cliente@email.com",
                required = true
        )
        String email,

        @Schema(
                description = "Identificador del tipo de préstamo",
                example = "523b3307-7d27-4165-b942-5cd7dbbc328d",
                required = true
        )
        String loanTypeId,

        @Schema(
                description = "Número de documento de identidad del solicitante",
                example = "22229850",
                required = true
        )
        String identityDocument
) {}