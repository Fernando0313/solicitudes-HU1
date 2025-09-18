package com.first.challenge.sqs.sender.approvedApplications;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ApprovedApplicationEvent {
    private UUID applicationId;
    private String status;
    private BigDecimal amount;
    private Integer contador;
    private String timestamp;
}