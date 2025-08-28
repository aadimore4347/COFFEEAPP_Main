-- Sample Data Insert Script for Coffee Machine Monitoring System
-- This script provides comprehensive test data for development and testing

-- ======================================================================================
-- FACILITIES
-- ======================================================================================
INSERT INTO facility (id, name, location, is_active, created_at, updated_at) VALUES
(1, 'Downtown Office', '123 Main St, Downtown Financial District', true, NOW(), NOW()),
(2, 'Tech Campus', '456 Innovation Blvd, Silicon Valley Tech District', true, NOW(), NOW()),
(3, 'Branch Office', '789 Corporate Ave, Business Park West', true, NOW(), NOW()),
(4, 'Research Center', '321 Science Dr, University District', true, NOW(), NOW()),
(5, 'Remote Hub', '555 Coworking Plaza, Suburban Center', true, NOW(), NOW());

-- ======================================================================================
-- USERS (Password: 'password' - bcrypt hash)
-- ======================================================================================
-- Facility Users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(1, 'downtown_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, NOW(), NOW()),
(2, 'tech_supervisor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 2, true, NOW(), NOW()),
(3, 'branch_coordinator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 3, true, NOW(), NOW()),
(4, 'research_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 4, true, NOW(), NOW()),
(5, 'hub_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 5, true, NOW(), NOW());

-- Admin Users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(6, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, NOW(), NOW()),
(7, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, NOW(), NOW());

-- ======================================================================================
-- COFFEE MACHINES
-- ======================================================================================
-- Downtown Office Machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(1, 1, 'ON', 92.5, 85, 75, 90, true, NOW(), NOW()),
(2, 1, 'ON', 91.0, 18, 15, 65, true, NOW(), NOW());

-- Tech Campus Machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(3, 2, 'OFF', 25.0, 95, 85, 80, true, NOW(), NOW()),
(4, 2, 'ERROR', 88.0, 12, 8, 25, true, NOW(), NOW()),
(5, 2, 'ON', 93.2, 70, 60, 55, true, NOW(), NOW()),
(6, 2, 'ON', 90.8, 45, 52, 78, true, NOW(), NOW());

-- Branch Office Machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(7, 3, 'ON', 91.5, 65, 45, 72, true, NOW(), NOW()),
(8, 3, 'ON', 92.8, 89, 82, 88, true, NOW(), NOW());

-- Research Center Machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(9, 4, 'ON', 93.0, 78, 68, 82, true, NOW(), NOW()),
(10, 4, 'ERROR', 85.5, 25, 15, 35, true, NOW(), NOW()),
(11, 4, 'ON', 91.8, 92, 88, 95, true, NOW(), NOW());

-- Remote Hub Machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(12, 5, 'ON', 92.2, 55, 38, 62, true, NOW(), NOW()),
(13, 5, 'OFF', 22.0, 88, 92, 85, true, NOW(), NOW());

-- ======================================================================================
-- USAGE HISTORY
-- ======================================================================================
-- Recent usage (last 24 hours)
INSERT INTO usage_history (id, machine_id, timestamp, brew_type, volume_ml, temp_at_brew, is_active, created_at, updated_at) VALUES
-- Morning rush (7-9 AM)
(1, 1, NOW() - INTERVAL 2 HOUR, 'ESPRESSO', 30, 92.5, true, NOW(), NOW()),
(2, 1, NOW() - INTERVAL 2 HOUR + INTERVAL 15 MINUTE, 'CAPPUCCINO', 150, 91.8, true, NOW(), NOW()),
(3, 2, NOW() - INTERVAL 1 HOUR + INTERVAL 45 MINUTE, 'LATTE', 200, 91.2, true, NOW(), NOW()),
(4, 2, NOW() - INTERVAL 1 HOUR + INTERVAL 30 MINUTE, 'AMERICANO', 250, 91.0, true, NOW(), NOW()),

-- Tech Campus - High volume
(5, 5, NOW() - INTERVAL 3 HOUR, 'ESPRESSO', 30, 93.0, true, NOW(), NOW()),
(6, 5, NOW() - INTERVAL 3 HOUR + INTERVAL 10 MINUTE, 'ESPRESSO', 30, 93.2, true, NOW(), NOW()),
(7, 6, NOW() - INTERVAL 2 HOUR + INTERVAL 30 MINUTE, 'CAPPUCCINO', 150, 90.8, true, NOW(), NOW()),
(8, 6, NOW() - INTERVAL 2 HOUR + INTERVAL 45 MINUTE, 'LATTE', 200, 91.0, true, NOW(), NOW()),

-- Lunch time (12-2 PM)
(9, 7, NOW() - INTERVAL 5 HOUR, 'AMERICANO', 250, 91.5, true, NOW(), NOW()),
(10, 8, NOW() - INTERVAL 5 HOUR + INTERVAL 20 MINUTE, 'FLAT_WHITE', 160, 92.8, true, NOW(), NOW()),

-- Research Center - Steady usage
(11, 9, NOW() - INTERVAL 4 HOUR, 'ESPRESSO', 30, 93.0, true, NOW(), NOW()),
(12, 11, NOW() - INTERVAL 3 HOUR + INTERVAL 30 MINUTE, 'MOCHA', 200, 91.8, true, NOW(), NOW()),

-- Remote Hub
(13, 12, NOW() - INTERVAL 6 HOUR, 'CAPPUCCINO', 150, 92.2, true, NOW(), NOW());

-- ======================================================================================
-- ALERTS
-- ======================================================================================
INSERT INTO alert (id, machine_id, type, message, severity, is_active, created_at, updated_at) VALUES
(1, 2, 'LOW_WATER', 'Water level is below 20%', 'WARNING', true, NOW(), NOW()),
(2, 2, 'LOW_MILK', 'Milk level is below 20%', 'WARNING', true, NOW(), NOW()),
(3, 4, 'LOW_BEANS', 'Beans level is below 30%', 'WARNING', true, NOW(), NOW()),
(4, 10, 'ERROR_STATUS', 'Machine is in ERROR state', 'CRITICAL', true, NOW(), NOW());

-- ======================================================================================
-- MAINTENANCE RECORDS
-- ======================================================================================
INSERT INTO maintenance_record (id, machine_id, type, description, performed_by, performed_at, is_active, created_at, updated_at) VALUES
(1, 1, 'CLEANING', 'Daily cleaning and descaling', 1, NOW() - INTERVAL 1 DAY, true, NOW(), NOW()),
(2, 3, 'REPAIR', 'Fixed water pump issue', 2, NOW() - INTERVAL 3 DAY, true, NOW(), NOW()),
(3, 5, 'PREVENTIVE', 'Monthly maintenance check', 2, NOW() - INTERVAL 1 WEEK, true, NOW(), NOW());

-- ======================================================================================
-- SENSOR DATA (for MQTT testing)
-- ======================================================================================
-- This table would store real-time sensor data from MQTT
-- CREATE TABLE IF NOT EXISTS sensor_data (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     machine_id BIGINT NOT NULL,
--     sensor_type VARCHAR(50) NOT NULL,
--     value DECIMAL(10,2) NOT NULL,
--     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (machine_id) REFERENCES coffee_machine(id)
-- );

-- Sample sensor data
-- INSERT INTO sensor_data (machine_id, sensor_type, value, timestamp) VALUES
-- (1, 'TEMPERATURE', 92.5, NOW()),
-- (1, 'WATER_LEVEL', 85, NOW()),
-- (1, 'MILK_LEVEL', 75, NOW()),
-- (1, 'BEANS_LEVEL', 90, NOW()),
-- (2, 'TEMPERATURE', 91.0, NOW()),
-- (2, 'WATER_LEVEL', 18, NOW()),
-- (2, 'MILK_LEVEL', 15, NOW()),
-- (2, 'BEANS_LEVEL', 65, NOW());