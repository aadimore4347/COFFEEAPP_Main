package com.example.coffeemachine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application-specific configuration properties.
 * 
 * Binds configuration values from application.yml under the 'app' prefix.
 * Provides type-safe access to application settings throughout the codebase.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Alerts alerts = new Alerts();
    private Dashboard dashboard = new Dashboard();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMs;
        private long refreshExpirationMs;
    }

    @Data
    public static class Alerts {
        private long debounceIntervalMs;
        private Thresholds thresholds = new Thresholds();

        @Data
        public static class Thresholds {
            private int waterLevel;
            private int milkLevel;
            private int beansLevel;
        }
    }

    @Data
    public static class Dashboard {
        private long facilityRefreshMs;
        private long adminRefreshMs;
    }
}