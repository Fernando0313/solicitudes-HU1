package com.first.challenge.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Loans",
                version = "1.0",
                description = "Documentación de la API de préstamos"
        )
)
public class SwaggerConfig {
}
