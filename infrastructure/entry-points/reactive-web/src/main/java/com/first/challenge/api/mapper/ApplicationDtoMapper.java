package com.first.challenge.api.mapper;

import com.first.challenge.api.dto.LoanApplicationDto;
import com.first.challenge.api.dto.LoanApplicationResponseDto;
import com.first.challenge.model.application.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationDtoMapper {
    @Mapping(target = "identityDocument", ignore = true)
    LoanApplicationDto toResponse(Application application);
    LoanApplicationResponseDto toSummary(Application application);
    Application toEntity(LoanApplicationDto dto);
}
