-- Development seed data for Coffee Machine Monitoring System
-- This file is loaded only in the 'dev' profile

-- Insert test facilities
INSERT INTO facility (id, name, location, is_active, created_at, updated_at) VALUES
(1, 'Downtown Office', '123 Main St, Downtown', true, NOW(), NOW()),
(2, 'Tech Campus', '456 Innovation Blvd, Tech District', true, NOW(), NOW()),
(3, 'Branch Office', '789 Corporate Ave, Business Park', true, NOW(), NOW());

-- Insert test users (password is 'password' hashed with bcrypt)
-- FACILITY users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(1, 'facility1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, NOW(), NOW()),
(2, 'facility2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 2, true, NOW(), NOW()),
(3, 'facility3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 3, true, NOW(), NOW());

-- ADMIN users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(4, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, NOW(), NOW()),
(5, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, NOW(), NOW());

-- Insert test coffee machines
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(1, 1, 'ON', 92.5, 85, 75, 90, true, NOW(), NOW()),
(2, 1, 'ON', 91.0, 45, 30, 65, true, NOW(), NOW()),
(3, 2, 'OFF', 25.0, 95, 85, 80, true, NOW(), NOW()),
(4, 2, 'ERROR', 88.0, 15, 20, 40, true, NOW(), NOW()),
(5, 3, 'ON', 93.0, 70, 60, 55, true, NOW(), NOW());

-- Insert some usage history
INSERT INTO usage_history (id, machine_id, timestamp, brew_type, volume_ml, temp_at_brew, is_active, created_at, updated_at) VALUES
(1, 1, NOW() - INTERVAL 1 HOUR, 'ESPRESSO', 30, 92.5, true, NOW(), NOW()),
(2, 1, NOW() - INTERVAL 2 HOUR, 'CAPPUCCINO', 150, 91.8, true, NOW(), NOW()),
(3, 2, NOW() - INTERVAL 30 MINUTE, 'LATTE', 200, 91.2, true, NOW(), NOW()),
(4, 3, NOW() - INTERVAL 3 HOUR, 'AMERICANO', 250, 92.0, true, NOW(), NOW()),
(5, 5, NOW() - INTERVAL 15 MINUTE, 'ESPRESSO', 30, 93.0, true, NOW(), NOW());

-- Insert some alerts
INSERT INTO alert (id, machine_id, type, severity, message, threshold_value, resolved, is_active, created_at, updated_at) VALUES
(1, 4, 'LOW_WATER', 'WARNING', 'Water level below threshold', 15, false, true, NOW(), NOW()),
(2, 4, 'LOW_MILK', 'WARNING', 'Milk level below threshold', 20, false, true, NOW(), NOW()),
(3, 4, 'MALFUNCTION', 'CRITICAL', 'Machine in error state', NULL, false, true, NOW(), NOW()),
(4, 2, 'LOW_MILK', 'WARNING', 'Milk level below threshold', 30, true, true, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY);

-- Note: This seed data provides:
-- - 3 facilities with different locations
-- - 5 users (3 facility users, 2 admins) all with password 'password'
-- - 5 coffee machines across facilities with varied states
-- - Sample usage history showing different brew types
-- - Active and resolved alerts for testing