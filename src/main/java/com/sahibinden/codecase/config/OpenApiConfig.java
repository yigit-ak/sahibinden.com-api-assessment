package com.sahibinden.codecase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Classifieds API",
                version = "v1",
                description = "Endpoints for classifieds, status updates and dashboard statistics.",
                contact = @Contact(name = "yigit-ak")
        )
)
@Configuration
public class OpenApiConfig {
}
