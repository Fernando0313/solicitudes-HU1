package com.first.challenge.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("loan_type")
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanTypeEntity {
    @Id
    @Column("loan_type_id")
    private UUID loanTypeId;
    private String name;
    @Column("minimum_amount")
    private BigDecimal minimumAmount;
    @Column("maximum_amount")
    private BigDecimal maximumAmount;
    @Column("interest_rate")
    private BigDecimal interestRate;
    @Column("automatic_validation")
    private Boolean automaticValidation;
}
