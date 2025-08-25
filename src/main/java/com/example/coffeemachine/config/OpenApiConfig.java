package com.example.coffeemachine.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coffeeMachineOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coffee Machine Monitoring API")
                        .description("APIs for managing facilities, machines, and real-time telemetry")
                        .version("v1.0.0")
                        .contact(new Contact().name("Coffee Ops").email("ops@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Docs")
                        .url("https://example.com/docs"));
    }
}