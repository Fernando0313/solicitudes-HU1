package com.first.challenge.model.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthenticatedUser {
    private String userId;
    private String identityDocument;
    private String email;
    private String firstName;
    private String lastName;
    private String roleId;
    private List<String> permissions;
}
