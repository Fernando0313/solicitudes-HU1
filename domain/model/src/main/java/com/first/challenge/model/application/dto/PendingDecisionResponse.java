package com.first.challenge.model.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;
@Value
@Builder
@Getter
@Setter
public class PendingDecisionResponse {
    UUID applicationId;
    BigDecimal amount;
    Integer term;
    String email;
    String loanTypeName;
    BigDecimal interestRate;
    String stateName;
    BigDecimal baseSalary;
    BigDecimal monthlyAmount;
}
