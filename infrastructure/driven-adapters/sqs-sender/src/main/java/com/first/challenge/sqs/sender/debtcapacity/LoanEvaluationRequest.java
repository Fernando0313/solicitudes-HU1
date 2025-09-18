package com.first.challenge.sqs.sender.debtcapacity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanEvaluationRequest {
    private ApplicationPayload application;
    private ApplicantPayload applicant;
    private LoanTypePayload loanType;
    private List<ApprovedLoanPayload> approvedLoans;
    private PolicyPayload policy;
    private EmailPayload email;

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class ApplicationPayload {
        private UUID applicationId;
        private BigDecimal amount;
        private Integer termMonths;
        private String email;
        private UUID loanTypeId;
        private String identityDocument;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class ApplicantPayload {
        private String identityDocument;
        private BigDecimal baseSalaryMonthly;
        private BigDecimal totalMonthlyIncome;
        private String currency;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class LoanTypePayload {
        private UUID loanTypeId;
        private String name;
        private BigDecimal minimumAmount;
        private BigDecimal maximumAmount;
        private BigDecimal annualInterestRate;
        private Boolean automaticValidation;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class ApprovedLoanPayload {
        private UUID loanId;
        private BigDecimal principalRemaining;
        private BigDecimal annualInterestRate;
        private Integer remainingTermMonths;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class PolicyPayload {
        private Double maxDebtRatio;
        private Integer manualReviewSalaryMultiple;
    }

    @Data @Builder @AllArgsConstructor @NoArgsConstructor
    public static class EmailPayload {
        private String to;
        private Boolean sendNotification;
    }
}

