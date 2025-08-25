package com.first.challenge.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class ApplicationPath {
    private String solicitud;
    private String solicitudById;
}
