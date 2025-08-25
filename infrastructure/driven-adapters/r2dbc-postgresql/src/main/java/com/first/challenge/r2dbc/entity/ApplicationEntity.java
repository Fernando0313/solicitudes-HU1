package com.first.challenge.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationEntity {
    @Id
    @Column("application_id")
    private UUID applicationId;
    private BigDecimal amount;
    private Integer term;
    private String email;
    @Column("state_id")
    private UUID stateId;
    @Column("loan_type_id")
    private UUID loanTypeId;
}
