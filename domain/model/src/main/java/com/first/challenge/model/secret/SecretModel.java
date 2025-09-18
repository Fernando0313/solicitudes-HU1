package com.first.challenge.model.secret;

import lombok.Data;

@Data
public class SecretModel {
    private String sqsnotification;
    private String dbSolicitudUser;
    private String dbSolicitudpassword;
    private String authUrl;
    private String sqsendeudamiento;
    private String dbSolicitudHost;
    private String dbSolicitudDatabase;
}

