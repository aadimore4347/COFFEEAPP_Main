package com.example.coffeemachine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {
    // Additional auditing setup (e.g., AuditorAware) can be added when user context is available
}