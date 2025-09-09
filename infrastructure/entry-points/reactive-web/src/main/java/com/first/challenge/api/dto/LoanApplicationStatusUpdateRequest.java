package com.first.challenge.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class LoanApplicationStatusUpdateRequest {
    private UUID applicationId;
    private String status;
}
