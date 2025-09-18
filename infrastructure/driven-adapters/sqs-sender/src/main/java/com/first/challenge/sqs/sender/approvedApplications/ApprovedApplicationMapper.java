package com.first.challenge.sqs.sender.approvedApplications;


import com.first.challenge.model.application.Application;

import java.time.Instant;

public class ApprovedApplicationMapper {

    public static ApprovedApplicationEvent toEvent(Application application) {
        return ApprovedApplicationEvent.builder()
                .applicationId(application.getApplicationId())
                .status("APROBADO")
                .amount(application.getAmount())
                .contador(1)
                .timestamp(Instant.now().toString())
                .build();
    }
}
