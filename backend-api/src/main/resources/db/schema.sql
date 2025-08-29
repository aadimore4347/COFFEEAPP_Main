-- Database Schema for Coffee Machine Monitoring System
-- This file creates the database structure

-- ======================================================================================
-- FACILITIES TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ======================================================================================
-- USERS TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role ENUM('ADMIN', 'FACILITY') NOT NULL,
    facility_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (facility_id) REFERENCES facility(id)
);

-- ======================================================================================
-- COFFEE MACHINES TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS coffee_machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    facility_id BIGINT NOT NULL,
    status ENUM('ON', 'OFF', 'ERROR', 'MAINTENANCE') DEFAULT 'OFF',
    temperature DECIMAL(5,2) DEFAULT 22.0,
    water_level DECIMAL(5,2) DEFAULT 100.0,
    milk_level DECIMAL(5,2) DEFAULT 100.0,
    beans_level DECIMAL(5,2) DEFAULT 100.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (facility_id) REFERENCES facility(id)
);

-- ======================================================================================
-- USAGE HISTORY TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS usage_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    brew_type ENUM('ESPRESSO', 'CAPPUCCINO', 'LATTE', 'AMERICANO', 'FLAT_WHITE', 'MOCHA') NOT NULL,
    volume_ml INT NOT NULL,
    temp_at_brew DECIMAL(5,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (machine_id) REFERENCES coffee_machine(id)
);

-- ======================================================================================
-- ALERTS TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    type ENUM('LOW_WATER', 'LOW_MILK', 'LOW_BEANS', 'HIGH_TEMPERATURE', 'ERROR_STATUS', 'MAINTENANCE_DUE') NOT NULL,
    message TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (machine_id) REFERENCES coffee_machine(id)
);

-- ======================================================================================
-- MAINTENANCE RECORDS TABLE
-- ======================================================================================
CREATE TABLE IF NOT EXISTS maintenance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    type ENUM('CLEANING', 'DESCALING', 'REPAIR', 'INSPECTION', 'REPLACEMENT') NOT NULL,
    description TEXT,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_due_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (machine_id) REFERENCES coffee_machine(id)
);

-- ======================================================================================
-- INDEXES FOR PERFORMANCE
-- ======================================================================================
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_role ON user(role);
CREATE INDEX idx_user_facility ON user(facility_id);
CREATE INDEX idx_machine_facility ON coffee_machine(facility_id);
CREATE INDEX idx_machine_status ON coffee_machine(status);
CREATE INDEX idx_usage_machine ON usage_history(machine_id);
CREATE INDEX idx_usage_timestamp ON usage_history(timestamp);
CREATE INDEX idx_alert_machine ON alert(machine_id);
CREATE INDEX idx_alert_type ON alert(type);
CREATE INDEX idx_maintenance_machine ON maintenance_record(machine_id);
CREATE INDEX idx_maintenance_type ON maintenance_record(type);