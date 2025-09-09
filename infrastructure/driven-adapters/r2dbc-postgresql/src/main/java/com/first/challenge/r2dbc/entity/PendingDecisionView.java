package com.first.challenge.r2dbc.entity;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("v_pending_decisions")
@Value
public class PendingDecisionView {
    @Id
    UUID applicationId;
    BigDecimal amount;
    Integer term;
    String email;
    @Column("minimum_amount")
    String loanTypeName;
    @Column("minimum_amount")
    BigDecimal interestRate;
    @Column("state_name")
    String stateName;
}