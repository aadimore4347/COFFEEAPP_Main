package com.example.coffeemachine.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for OpenAPI documentation (Swagger UI).
 * 
 * Provides comprehensive API documentation with JWT authentication support.
 * Access Swagger UI at: http://localhost:8080/api/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI coffeeMachineMonitoringOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .addSecurityItem(createSecurityRequirement())
                .components(createComponents());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Coffee Machine Monitoring API")
                .description("""
                    Comprehensive Coffee Machine Monitoring System API
                    
                    **Features:**
                    - Real-time MQTT monitoring of coffee machines
                    - Role-based access control (FACILITY, ADMIN)
                    - JWT authentication and refresh tokens
                    - Machine status and supply level tracking
                    - Usage history and analytics
                    - Automated alerting system
                    - Facility and machine management
                    
                    **Authentication:**
                    1. Login with credentials via `/auth/login`
                    2. Use returned JWT token in Authorization header: `Bearer <token>`
                    3. Refresh tokens via `/auth/refresh` when needed
                    
                    **User Roles:**
                    - **FACILITY**: Access to assigned machines and basic operations
                    - **ADMIN**: Full system access including facility management
                    """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Coffee Machine Monitoring Team")
                        .email("support@example.com")
                        .url("https://example.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("Development server"),
                new Server()
                        .url("https://api.coffee-monitoring.example.com" + contextPath)
                        .description("Production server")
        );
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement()
                .addList("bearerAuth");
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("""
                            JWT Authorization header using the Bearer scheme.
                            
                            Enter your JWT token in the format: Bearer <token>
                            
                            Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                            """));
    }
}