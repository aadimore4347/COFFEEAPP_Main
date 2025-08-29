-- H2-compatible Development Database Setup
-- This single script drops, creates, and populates all tables.

-- Drop existing objects to ensure a clean slate
DROP TABLE IF EXISTS alert;
DROP TABLE IF EXISTS usage_history;
DROP TABLE IF EXISTS coffee_machine;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS facility;

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
    CONSTRAINT fk_machine_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE ON UPDATE CASCADE
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
    CONSTRAINT fk_usage_machine FOREIGN KEY (machine_id) REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE
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
    CONSTRAINT fk_alert_machine FOREIGN KEY (machine_id) REFERENCES coffee_machine(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ======================================================================================
-- DATA INSERTION
-- Password for all users is 'password' (bcrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6)
-- ======================================================================================

INSERT INTO facility (id, name, location, is_active, created_at, updated_at) VALUES
(1, 'Downtown Office', '123 Main St, Downtown Financial District', true, '2024-01-15 08:00:00', '2024-01-15 08:00:00'),
(2, 'Tech Campus', '456 Innovation Blvd, Silicon Valley Tech District', true, '2024-01-20 09:30:00', '2024-01-20 09:30:00'),
(3, 'Branch Office', '789 Corporate Ave, Business Park West', true, '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(4, 'Research Center', '321 Science Dr, University District', true, '2024-02-10 11:00:00', '2024-02-10 11:00:00'),
(5, 'Remote Hub', '555 Coworking Plaza, Suburban Center', true, '2024-02-15 14:30:00', '2024-02-15 14:30:00');

INSERT INTO "user" (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(1, 'downtown_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-01-15 08:30:00', '2024-01-15 08:30:00'),
(2, 'tech_supervisor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 2, true, '2024-01-20 10:00:00', '2024-01-20 10:00:00'),
(3, 'branch_coordinator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 3, true, '2024-02-01 10:45:00', '2024-02-01 10:45:00'),
(4, 'research_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 4, true, '2024-02-10 11:30:00', '2024-02-10 11:30:00'),
(5, 'hub_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 5, true, '2024-02-15 15:00:00', '2024-02-15 15:00:00'),
(6, 'facilities_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-02-20 09:00:00', '2024-02-20 09:00:00'),
(7, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(8, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(9, 'system_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-12 09:00:00', '2024-01-12 09:00:00');

INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(1, 1, 'ON', 92.5, 85, 75, 90, true, '2024-01-15 08:45:00', '2024-01-15 08:50:00'),
(2, 1, 'ON', 91.0, 18, 15, 65, true, '2024-01-15 09:00:00', '2024-01-15 09:03:00'),
(3, 2, 'OFF', 25.0, 95, 85, 80, true, '2024-01-20 10:30:00', '2024-01-20 10:45:00'),
(4, 2, 'ERROR', 88.0, 12, 8, 25, true, '2024-01-20 10:45:00', '2024-01-20 10:47:00'),
(5, 2, 'ON', 93.2, 70, 60, 55, true, '2024-01-20 11:00:00', '2024-01-20 11:01:00'),
(6, 2, 'ON', 90.8, 45, 52, 78, true, '2024-01-20 11:15:00', '2024-01-20 11:08:00'),
(7, 3, 'ON', 91.5, 65, 45, 72, true, '2024-02-01 11:00:00', '2024-02-01 11:10:00'),
(8, 3, 'ON', 92.8, 89, 82, 88, true, '2024-02-01 11:30:00', '2024-02-01 11:36:00'),
(9, 4, 'ON', 93.0, 78, 68, 82, true, '2024-02-10 12:00:00', '2024-02-10 12:04:00'),
(10, 4, 'ERROR', 85.5, 25, 15, 35, true, '2024-02-10 12:15:00', '2024-02-10 12:07:00'),
(11, 4, 'ON', 91.8, 92, 88, 95, true, '2024-02-10 12:30:00', '2024-02-10 12:28:00'),
(12, 5, 'ON', 92.2, 55, 38, 62, true, '2024-02-15 15:30:00', '2024-02-15 15:18:00'),
(13, 5, 'OFF', 22.0, 88, 92, 85, true, '2024-02-15 16:00:00', '2024-02-15 15:40:00');

INSERT INTO usage_history (id, machine_id, timestamp, brew_type, volume_ml, temp_at_brew, is_active, created_at, updated_at) VALUES
(1, 1, '2024-03-10 08:00:00', 'ESPRESSO', 30, 92.5, true, '2024-03-10 08:00:00', '2024-03-10 08:00:00');

INSERT INTO alert (id, machine_id, type, severity, message, threshold_value, resolved, is_active, created_at, updated_at) VALUES
(1, 4, 'MALFUNCTION', 'CRITICAL', 'Machine has encountered a critical error and requires immediate maintenance', NULL, false, true, '2024-03-10 08:00:00', '2024-03-10 08:00:00');
