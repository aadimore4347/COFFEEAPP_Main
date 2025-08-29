-- H2-compatible Development Schema
-- This script creates the tables for the 'dev' profile.

-- Drop existing objects to ensure a clean slate
DROP VIEW IF EXISTS v_active_alerts;
DROP VIEW IF EXISTS v_recent_usage;
DROP VIEW IF EXISTS v_active_machines;
DROP TABLE IF EXISTS alert;
DROP TABLE IF EXISTS usage_history;
DROP TABLE IF EXISTS coffee_machine;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS facility;
DROP TABLE IF EXISTS schema_version;

-- ======================================================================================
-- SCHEMA CREATION
-- ======================================================================================

CREATE TABLE facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "user" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL,
    facility_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT chk_user_role CHECK (role IN ('FACILITY', 'ADMIN'))
);

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_machine_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_machine_status CHECK (status IN ('ON', 'OFF', 'ERROR')),
    CONSTRAINT chk_machine_temperature CHECK (temperature IS NULL OR (temperature >= 0 AND temperature <= 150)),
    CONSTRAINT chk_machine_water_level CHECK (water_level IS NULL OR (water_level >= 0 AND water_level <= 100)),
    CONSTRAINT chk_machine_milk_level CHECK (milk_level IS NULL OR (milk_level >= 0 AND milk_level <= 100)),
    CONSTRAINT chk_machine_beans_level CHECK (beans_level IS NULL OR (beans_level >= 0 AND beans_level <= 100))
);

CREATE TABLE usage_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    brew_type VARCHAR(20) NOT NULL,
    volume_ml INTEGER NULL,
    temp_at_brew DECIMAL(5,2) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usage_machine FOREIGN KEY (machine_id) REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_usage_brew_type CHECK (brew_type IN ('ESPRESSO', 'DOUBLE_ESPRESSO', 'AMERICANO', 'CAPPUCCINO', 'LATTE', 'MACCHIATO', 'MOCHA', 'FLAT_WHITE')),
    CONSTRAINT chk_usage_volume CHECK (volume_ml IS NULL OR (volume_ml > 0 AND volume_ml <= 500)),
    CONSTRAINT chk_usage_temp CHECK (temp_at_brew IS NULL OR (temp_at_brew >= 70 AND temp_at_brew <= 100))
);

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_machine FOREIGN KEY (machine_id) REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_alert_type CHECK (type IN ('LOW_WATER', 'LOW_MILK', 'LOW_BEANS', 'MALFUNCTION')),
    CONSTRAINT chk_alert_severity CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    CONSTRAINT chk_alert_threshold CHECK (threshold_value IS NULL OR (threshold_value >= 0 AND threshold_value <= 100))
);
