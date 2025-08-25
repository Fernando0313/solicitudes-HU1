package com.first.challenge.api.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record LoanApplicationResponseDto(
        BigDecimal amount,
        Integer term,
        String email,
        UUID loanTypeId,
        UUID stateId
) {
}
