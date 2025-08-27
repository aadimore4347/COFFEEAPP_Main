package com.example.coffeemachine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Configuration for JPA auditing functionality.
 * 
 * Enables automatic population of:
 * - @CreatedDate fields with entity creation timestamp
 * - @LastModifiedDate fields with entity update timestamp
 * 
 * All entities with auditing fields will automatically have these values
 * populated during persistence operations.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditingConfig {

    /**
     * Provides the current timestamp for auditing fields.
     * Uses LocalDateTime for consistent timezone handling.
     * 
     * @return DateTimeProvider that supplies current LocalDateTime
     */
    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}