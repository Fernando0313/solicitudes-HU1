package com.first.challenge.model.application;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {
    private UUID applicationId;
    private BigDecimal amount;
    private BigDecimal baseSalary;
    private Integer term;
    private String email;
    private UUID stateId;
    private UUID loanTypeId;
}
