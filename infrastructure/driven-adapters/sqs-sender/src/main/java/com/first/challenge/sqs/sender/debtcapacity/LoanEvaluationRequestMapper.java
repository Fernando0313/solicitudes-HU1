package com.first.challenge.sqs.sender.debtcapacity;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.model.loantype.LoanType;

import com.first.challenge.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanEvaluationRequestMapper {
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationRepository applicationRepository;

    public Mono<LoanEvaluationRequest> toRequest(Application application) {
        return loanTypeRepository.findById(application.getLoanTypeId())
                .doOnNext(req -> System.out.println("✅ Mapper OK2: " + req))
                .doOnError(err -> System.err.println("❌ Error en mapper.toRequest2: " + err.getMessage()))
                .flatMap(loanType ->
                        applicationRepository.findApprovedByEmail(application.getEmail())
                                .doOnNext(req -> System.out.println("✅ Mapper OK3: " + req))
                                .doOnError(err -> System.err.println("❌ Error en mapper.toRequest3: " + err.getMessage()))
                                //getIdentityDocument
                                .collectList()
                                .map(approvedLoans -> buildRequest(application, loanType, approvedLoans))
                );
    }

    private LoanEvaluationRequest buildRequest(Application app, LoanType loanType, List<Application> approvedLoans) {
        return LoanEvaluationRequest.builder()
                .application(LoanEvaluationRequest.ApplicationPayload.builder()
                        .applicationId(app.getApplicationId())
                        .amount(app.getAmount())
                        .termMonths(app.getTerm())
                        .email(app.getEmail())
                        .loanTypeId(app.getLoanTypeId())
                        //.identityDocument(app.getIdentityDocument())
                        .build())
                .applicant(LoanEvaluationRequest.ApplicantPayload.builder()
                       // .identityDocument(app.getIdentityDocument())
                        .baseSalaryMonthly(app.getBaseSalary())
                        .totalMonthlyIncome(app.getBaseSalary()) // asumimos salario = ingreso
                        .currency("PEN")
                        .build())
                .loanType(LoanEvaluationRequest.LoanTypePayload.builder()
                        .loanTypeId(loanType.getLoanTypeId())
                        .name(loanType.getName())
                        .minimumAmount(loanType.getMinimumAmount())
                        .maximumAmount(loanType.getMaximumAmount())
                        .annualInterestRate(loanType.getInterestRate())
                        .automaticValidation(loanType.getAutomaticValidation())
                        .build())
                .approvedLoans(
                        approvedLoans.stream().map(loan ->
                                LoanEvaluationRequest.ApprovedLoanPayload.builder()
                                        .loanId(loan.getApplicationId())
                                        .principalRemaining(loan.getAmount())
                                        .annualInterestRate(new BigDecimal("0.12")) // FIXME: deberías sacar tasa real
                                        .remainingTermMonths(loan.getTerm()) // FIXME: idem, calcula término restante
                                        .build()
                        ).toList()
                )
                .policy(LoanEvaluationRequest.PolicyPayload.builder()
                        .maxDebtRatio(0.35)
                        .manualReviewSalaryMultiple(5)
                        .build())
                .email(LoanEvaluationRequest.EmailPayload.builder()
                        .to(app.getEmail())
                        .sendNotification(true)
                        .build())
                .build();
    }
}

