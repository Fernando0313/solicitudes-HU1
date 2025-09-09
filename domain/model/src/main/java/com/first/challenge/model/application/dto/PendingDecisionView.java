package com.first.challenge.model.application.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value(staticConstructor = "of")
public class PendingDecisionView {
    UUID applicationId;
    BigDecimal amount;
    Integer term;
    String email;
    String loanTypeName;          // nombre del préstamo
    BigDecimal interestRate;      // tasa_interes
    String stateName;             // estado_solicitud
    BigDecimal baseSalary;        // salario_base (si lo tienes en otra tabla)
    BigDecimal monthlyAmount;     // monto_mensual_solicitud (calculado o persistido)
}