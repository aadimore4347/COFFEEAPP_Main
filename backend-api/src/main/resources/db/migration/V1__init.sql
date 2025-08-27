-- Coffee Machine Monitoring System - Initial Database Schema
-- Version: 1.0.0
-- Description: Creates all tables, constraints, and indexes for the coffee machine monitoring system

-- ======================================================================================
-- FACILITY TABLE
-- ======================================================================================
CREATE TABLE facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_facility_name (name),
    INDEX idx_facility_active (is_active),
    INDEX idx_facility_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================================
-- USER TABLE
-- ======================================================================================
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL,
    facility_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    CONSTRAINT fk_user_facility FOREIGN KEY (facility_id) 
        REFERENCES facility(id) ON DELETE SET NULL ON UPDATE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_user_role CHECK (role IN ('FACILITY', 'ADMIN')),
    
    -- Indexes
    INDEX idx_user_username (username),
    INDEX idx_user_facility (facility_id),
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active),
    INDEX idx_user_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================================
-- COFFEE_MACHINE TABLE
-- ======================================================================================
CREATE TABLE coffee_machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    facility_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'OFF',
    temperature DECIMAL(5,2) NULL,
    water_level INTEGER NULL,
    milk_level INTEGER NULL,
    beans_level INTEGER NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    CONSTRAINT fk_machine_facility FOREIGN KEY (facility_id) 
        REFERENCES facility(id) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_machine_status CHECK (status IN ('ON', 'OFF', 'ERROR')),
    CONSTRAINT chk_machine_temperature CHECK (temperature IS NULL OR (temperature >= 0 AND temperature <= 150)),
    CONSTRAINT chk_machine_water_level CHECK (water_level IS NULL OR (water_level >= 0 AND water_level <= 100)),
    CONSTRAINT chk_machine_milk_level CHECK (milk_level IS NULL OR (milk_level >= 0 AND milk_level <= 100)),
    CONSTRAINT chk_machine_beans_level CHECK (beans_level IS NULL OR (beans_level >= 0 AND beans_level <= 100)),
    
    -- Indexes
    INDEX idx_machine_facility (facility_id),
    INDEX idx_machine_status (status),
    INDEX idx_machine_levels (water_level, milk_level, beans_level),
    INDEX idx_machine_active (is_active),
    INDEX idx_machine_updated (updated_at),
    INDEX idx_machine_facility_status (facility_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================================
-- USAGE_HISTORY TABLE
-- ======================================================================================
CREATE TABLE usage_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    brew_type VARCHAR(20) NOT NULL,
    volume_ml INTEGER NULL,
    temp_at_brew DECIMAL(5,2) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    CONSTRAINT fk_usage_machine FOREIGN KEY (machine_id) 
        REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_usage_brew_type CHECK (brew_type IN (
        'ESPRESSO', 'DOUBLE_ESPRESSO', 'AMERICANO', 'CAPPUCCINO', 
        'LATTE', 'MACCHIATO', 'MOCHA', 'FLAT_WHITE'
    )),
    CONSTRAINT chk_usage_volume CHECK (volume_ml IS NULL OR (volume_ml > 0 AND volume_ml <= 500)),
    CONSTRAINT chk_usage_temp CHECK (temp_at_brew IS NULL OR (temp_at_brew >= 70 AND temp_at_brew <= 100)),
    
    -- Indexes
    INDEX idx_usage_machine (machine_id),
    INDEX idx_usage_timestamp (timestamp),
    INDEX idx_usage_brew_type (brew_type),
    INDEX idx_usage_machine_timestamp (machine_id, timestamp),
    INDEX idx_usage_active (is_active),
    INDEX idx_usage_created (created_at),
    INDEX idx_usage_recent (timestamp, machine_id) -- For recent usage queries
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================================
-- ALERT TABLE
-- ======================================================================================
CREATE TABLE alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    severity VARCHAR(10) NOT NULL,
    message VARCHAR(500) NOT NULL,
    threshold_value INTEGER NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    CONSTRAINT fk_alert_machine FOREIGN KEY (machine_id) 
        REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Check Constraints
    CONSTRAINT chk_alert_type CHECK (type IN ('LOW_WATER', 'LOW_MILK', 'LOW_BEANS', 'MALFUNCTION')),
    CONSTRAINT chk_alert_severity CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    CONSTRAINT chk_alert_threshold CHECK (threshold_value IS NULL OR (threshold_value >= 0 AND threshold_value <= 100)),
    
    -- Indexes
    INDEX idx_alert_machine (machine_id),
    INDEX idx_alert_type (type),
    INDEX idx_alert_severity (severity),
    INDEX idx_alert_resolved (resolved),
    INDEX idx_alert_machine_type (machine_id, type),
    INDEX idx_alert_unresolved (machine_id, resolved, created_at),
    INDEX idx_alert_active (is_active),
    INDEX idx_alert_created (created_at),
    INDEX idx_alert_critical (severity, resolved) -- For critical unresolved alerts
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================================
-- PERFORMANCE OPTIMIZATION VIEWS
-- ======================================================================================

-- View for active machines with current status
CREATE VIEW v_active_machines AS
SELECT 
    cm.id,
    cm.facility_id,
    f.name as facility_name,
    f.location as facility_location,
    cm.status,
    cm.temperature,
    cm.water_level,
    cm.milk_level,
    cm.beans_level,
    cm.updated_at as last_update,
    (SELECT COUNT(*) FROM alert a WHERE a.machine_id = cm.id AND a.resolved = FALSE AND a.is_active = TRUE) as unresolved_alerts
FROM coffee_machine cm
INNER JOIN facility f ON cm.facility_id = f.id
WHERE cm.is_active = TRUE AND f.is_active = TRUE;

-- View for recent usage statistics
CREATE VIEW v_recent_usage AS
SELECT 
    uh.machine_id,
    cm.facility_id,
    f.name as facility_name,
    uh.brew_type,
    COUNT(*) as usage_count,
    AVG(uh.volume_ml) as avg_volume,
    AVG(uh.temp_at_brew) as avg_temperature,
    DATE(uh.timestamp) as usage_date
FROM usage_history uh
INNER JOIN coffee_machine cm ON uh.machine_id = cm.id
INNER JOIN facility f ON cm.facility_id = f.id
WHERE uh.is_active = TRUE 
  AND uh.timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY uh.machine_id, cm.facility_id, f.name, uh.brew_type, DATE(uh.timestamp);

-- View for active alerts summary
CREATE VIEW v_active_alerts AS
SELECT 
    a.machine_id,
    cm.facility_id,
    f.name as facility_name,
    a.type,
    a.severity,
    a.message,
    a.threshold_value,
    a.created_at
FROM alert a
INNER JOIN coffee_machine cm ON a.machine_id = cm.id
INNER JOIN facility f ON cm.facility_id = f.id
WHERE a.is_active = TRUE 
  AND a.resolved = FALSE
  AND cm.is_active = TRUE 
  AND f.is_active = TRUE
ORDER BY 
    CASE a.severity 
        WHEN 'CRITICAL' THEN 1
        WHEN 'WARNING' THEN 2
        WHEN 'INFO' THEN 3
    END,
    a.created_at ASC;

-- ======================================================================================
-- ADDITIONAL PERFORMANCE INDEXES
-- ======================================================================================

-- Composite indexes for common query patterns
CREATE INDEX idx_facility_machines_active ON coffee_machine(facility_id, is_active, status);
CREATE INDEX idx_machine_supplies ON coffee_machine(water_level, milk_level, beans_level, is_active);
CREATE INDEX idx_usage_analytics ON usage_history(machine_id, brew_type, timestamp, is_active);
CREATE INDEX idx_alert_dashboard ON alert(machine_id, resolved, severity, created_at);

-- Indexes for time-based queries
CREATE INDEX idx_usage_hourly ON usage_history(DATE(timestamp), HOUR(timestamp), machine_id);
CREATE INDEX idx_alert_daily ON alert(DATE(created_at), machine_id, resolved);

-- ======================================================================================
-- INITIAL DATA CONSTRAINTS AND TRIGGERS
-- ======================================================================================

-- Ensure FACILITY users have facility assignments
DELIMITER //
CREATE TRIGGER trg_user_facility_check 
BEFORE INSERT ON user
FOR EACH ROW
BEGIN
    IF NEW.role = 'FACILITY' AND NEW.facility_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FACILITY users must be assigned to a facility';
    END IF;
    
    IF NEW.role = 'ADMIN' AND NEW.facility_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ADMIN users should not be assigned to a specific facility';
    END IF;
END//

CREATE TRIGGER trg_user_facility_check_update
BEFORE UPDATE ON user
FOR EACH ROW
BEGIN
    IF NEW.role = 'FACILITY' AND NEW.facility_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FACILITY users must be assigned to a facility';
    END IF;
    
    IF NEW.role = 'ADMIN' AND NEW.facility_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ADMIN users should not be assigned to a specific facility';
    END IF;
END//
DELIMITER ;

-- ======================================================================================
-- COMMENTS FOR DOCUMENTATION
-- ======================================================================================

-- Table comments
ALTER TABLE facility COMMENT = 'Facilities/buildings containing coffee machines';
ALTER TABLE user COMMENT = 'System users with role-based access control';
ALTER TABLE coffee_machine COMMENT = 'Coffee machines with real-time status and supply levels';
ALTER TABLE usage_history COMMENT = 'Historical record of coffee brewing activities';
ALTER TABLE alert COMMENT = 'System-generated alerts for machine issues and maintenance';

-- Column comments for key fields
ALTER TABLE facility MODIFY COLUMN name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Unique facility identifier name';
ALTER TABLE facility MODIFY COLUMN location VARCHAR(255) NOT NULL COMMENT 'Physical address or location description';

ALTER TABLE user MODIFY COLUMN username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Unique username for authentication';
ALTER TABLE user MODIFY COLUMN password_hash VARCHAR(60) NOT NULL COMMENT 'BCrypt hashed password';
ALTER TABLE user MODIFY COLUMN role VARCHAR(20) NOT NULL COMMENT 'User role: FACILITY or ADMIN';

ALTER TABLE coffee_machine MODIFY COLUMN status VARCHAR(10) NOT NULL DEFAULT 'OFF' COMMENT 'Current machine status: ON, OFF, or ERROR';
ALTER TABLE coffee_machine MODIFY COLUMN temperature DECIMAL(5,2) NULL COMMENT 'Current brewing temperature in Celsius';
ALTER TABLE coffee_machine MODIFY COLUMN water_level INTEGER NULL COMMENT 'Water supply level percentage (0-100)';
ALTER TABLE coffee_machine MODIFY COLUMN milk_level INTEGER NULL COMMENT 'Milk supply level percentage (0-100)';
ALTER TABLE coffee_machine MODIFY COLUMN beans_level INTEGER NULL COMMENT 'Coffee beans supply level percentage (0-100)';

ALTER TABLE usage_history MODIFY COLUMN timestamp TIMESTAMP NOT NULL COMMENT 'When the brewing operation occurred';
ALTER TABLE usage_history MODIFY COLUMN brew_type VARCHAR(20) NOT NULL COMMENT 'Type of beverage brewed';
ALTER TABLE usage_history MODIFY COLUMN volume_ml INTEGER NULL COMMENT 'Volume of brewed beverage in milliliters';
ALTER TABLE usage_history MODIFY COLUMN temp_at_brew DECIMAL(5,2) NULL COMMENT 'Brewing temperature at time of operation';

ALTER TABLE alert MODIFY COLUMN type VARCHAR(20) NOT NULL COMMENT 'Alert type: LOW_WATER, LOW_MILK, LOW_BEANS, MALFUNCTION';
ALTER TABLE alert MODIFY COLUMN severity VARCHAR(10) NOT NULL COMMENT 'Alert severity: INFO, WARNING, CRITICAL';
ALTER TABLE alert MODIFY COLUMN message VARCHAR(500) NOT NULL COMMENT 'Human-readable alert description';
ALTER TABLE alert MODIFY COLUMN threshold_value INTEGER NULL COMMENT 'Threshold value that triggered the alert';
ALTER TABLE alert MODIFY COLUMN resolved BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether the alert has been resolved';

-- ======================================================================================
-- SCHEMA VERSION TRACKING
-- ======================================================================================

-- Create a simple version tracking table for future migrations
CREATE TABLE schema_version (
    version VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Record this migration
INSERT INTO schema_version (version, description) 
VALUES ('1.0.0', 'Initial database schema with all core tables and relationships');

-- ======================================================================================
-- END OF MIGRATION V1__init.sql
-- ======================================================================================